package onim.en.etl.ui.custom;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.google.common.base.Strings;

import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.extension.freemarket.ImproveFreeMarket;
import onim.en.etl.extension.freemarket.PreventPurchase;
import onim.en.etl.extension.freemarket.PreventPurchase.ClickFreeMarketItem;
import onim.en.etl.util.GuiUtil;
import onim.en.etl.util.MinecraftUtil;

public class GuiFreeMarket extends GuiChest {

  private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
  private int inventoryRows;


  public ImproveFreeMarket extension;

  private static String searchString;
  
  private GuiTextField searchField;

  private boolean hideSearchBox = true;
  
  private List<Slot> searchResult = null;
  
  public GuiFreeMarket(ImproveFreeMarket extension, IInventory playerInv, IInventory chestInv) {
    super(playerInv, chestInv);
    this.extension = extension;
    this.inventoryRows = chestInv.getSizeInventory() / 9;
    
    this.searchResult = new LinkedList<>();
  }

  public void initGui() {
    super.initGui();

    this.searchField = new GuiTextField(
            5505, ExtendTheLow.AdvancedFont, this.width / 2 - 75, this.height / 2 - 6,
            150, 12
    );

    PreventPurchase.reset();
  }

  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    super.drawScreen(mouseX, mouseY, partialTicks);
    if (!this.hideSearchBox) {
      GlStateManager.disableDepth();
      this.searchField.drawTextBox();
      GlStateManager.enableDepth();
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    super.drawGuiContainerForegroundLayer(mouseX, mouseY);

    ClickFreeMarketItem clickInfo = PreventPurchase.getFreeMarketClickInfo();
    if (clickInfo == null) {
      return;
    }

    GlStateManager.pushMatrix();
    GlStateManager.translate(clickInfo.slot.xDisplayPosition, clickInfo.slot.yDisplayPosition, 500);

    GuiUtil.drawGradientRectHorizontal(0, 0, 16, 16, 0x66000000, 0x66000000);

    GlStateManager.popMatrix();
    float sec = 5F - (System.currentTimeMillis() - clickInfo.clickedAt) / 1000F;
    String s = I18n.format("onim.en.etl.improveFreeMarket.clickAgain", String.format("%.1f", sec));
    GuiUtil.renderTooltip(s, 8 + mouseX - guiLeft, 16 + mouseY - guiTop);
  }

  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    if (extension.improveBackgroundRender) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
      int i = (this.width - this.xSize) / 2;
      int j = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(i, j, 0, 0, this.xSize, 17);

      for (int y = 0; y < this.inventoryRows; y++) {
        this.drawTexturedModalRect(i, 17 + j + (18 * y), 0, 17, this.xSize, 18);
      }

      this.drawTexturedModalRect(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    } else {
      super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

    PreventPurchase.update();
  }

  protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
    if (this.extension.preventAccidentalPurchases) {

      if (slotIn != null && PreventPurchase.isFreeMarketItem(slotIn.getStack())) {
        PreventPurchase.clickFreeMarketItem(slotIn);
        if (!PreventPurchase.canPurchase(slotIn)) {
          return;
        }
      }
    }

    super.handleMouseClick(slotIn, slotId, clickedButton, clickType);
    this.updateSearchResult();
  }

  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
    if (!this.searchField.isFocused()) {
      super.mouseClicked(mouseX, mouseY, mouseButton);
    }
  }

  @Override
  protected void keyTyped(char typedChar, int keyCode) throws IOException {
    if (this.hideSearchBox && typedChar == '/') {
      this.hideSearchBox = false;
      this.searchField.setFocused(true);
      return;
    }

    if (typedChar == '\n' || typedChar == '\r') {
      this.hideSearchBox = true;
      this.searchField.setText("");
      return;
    }

    if (this.searchField.textboxKeyTyped(typedChar, keyCode)) {
      searchString = this.searchField.getText();
    } else {
      super.keyTyped(typedChar, keyCode);
    }
    this.updateSearchResult();
  }

  private void updateSearchResult() {
    this.searchResult.clear();

    for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
      Slot slot = (Slot)this.inventorySlots.inventorySlots.get(i);
      
      ItemStack stack = slot.getStack();

      if (this.hasRelation(stack, searchString)) {
        this.searchResult.add(slot);
      }
    }
  }
  
  private boolean hasRelation(ItemStack stack, String text) {
    if (stack == null || Strings.isNullOrEmpty(text)) {
      return false;
    }

    String name;
    
    if (stack.hasDisplayName()) {
      name = stack.getDisplayName();
    } else {
      name = I18n.format(stack.getUnlocalizedName());
    }

    System.out.println(name);
    
    if (name.contains(text)) {
      return true;
    }

    List<String> lore = MinecraftUtil.getLore(stack);
    
    return lore.stream().anyMatch(s -> s.contains(text));
  }
}
