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
import onim.en.etl.ExtendTheLow;
import onim.en.etl.util.MinecraftUtil;

public class GuiSearchChest extends GuiChest {

  private static String searchString = "";

  private GuiTextField searchField;

  private boolean hideSearchBox = true;

  private List<Slot> searchResult = null;

  private boolean initialized = false;

  public GuiSearchChest(IInventory playerInv, IInventory chestInv) {
    super(playerInv, chestInv);

    this.searchResult = new LinkedList<>();
  }

  public void initGui() {
    super.initGui();

    this.searchField = new GuiTextField(5505, ExtendTheLow.AdvancedFont, this.width / 2 - 75, this.height / 2
        - 6, 150, 12);

  }

  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    if (!initialized) {
      this.updateSearchResult();
      initialized = true;
    }

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

    for (Slot slot : searchResult) {
      GlStateManager.disableLighting();
      GlStateManager.disableDepth();
      int x = slot.xDisplayPosition;
      int y = slot.yDisplayPosition;
      GlStateManager.colorMask(true, true, true, false);

      GlStateManager.pushMatrix();
      GlStateManager.translate(x, y, 0);
      GlStateManager.color(1F, 0F, 0F, 1F);
      GlStateManager.disableTexture2D();
      GlStateManager.disableLighting();
      GL11.glLineWidth(4f);
      WorldRenderer buf = Tessellator.getInstance().getWorldRenderer();
      buf.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);

      buf.pos(-0.5, 0, 0).endVertex();
      buf.pos(16.5, 0, 0).endVertex();

      buf.pos(16, 0, 0).endVertex();
      buf.pos(16, 16, 0).endVertex();

      buf.pos(16.5, 16, 0).endVertex();
      buf.pos(-0.5, 16, 0).endVertex();

      buf.pos(0, 16, 0).endVertex();
      buf.pos(0, 0, 0).endVertex();

      Tessellator.getInstance().draw();

      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.enableLighting();
      GlStateManager.enableDepth();
      GlStateManager.popMatrix();
      GlStateManager.enableTexture2D();
    }

  }

  @Override
  public void drawDefaultBackground() {
    super.drawDefaultBackground();

    if (Strings.isNullOrEmpty(searchString)) {
      String text = I18n.format("onim.en.etl.chestSearchBox.help1");
      ExtendTheLow.AdvancedFont.drawString(text, 4, 4, 0xFFFFFF);
    } else {
      String text = I18n.format("onim.en.etl.chestSearchBox.help2", searchString);
      ExtendTheLow.AdvancedFont.drawString(text, 4, 4, 0xFFFFFF);
    }
  }

  protected void handleMouseClick(Slot slotIn, int slotId, int clickedButton, int clickType) {
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
    if (typedChar == '\n' || typedChar == '\r') {
      this.hideSearchBox = !this.hideSearchBox;

      if (this.hideSearchBox) {
        this.searchField.setText("");
      } else {
        this.searchField.setFocused(true);
      }
      return;
    }

    if (!this.hideSearchBox && this.searchField.textboxKeyTyped(typedChar, keyCode)) {
      searchString = this.searchField.getText();
    } else {
      super.keyTyped(typedChar, keyCode);
    }
    this.updateSearchResult();
  }

  private void updateSearchResult() {
    this.searchResult.clear();

    for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i++) {
      Slot slot = (Slot) this.inventorySlots.inventorySlots.get(i);

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

    String name = I18n.format(stack.getDisplayName()).toLowerCase();

    if (name.contains(text.toLowerCase())) {
      return true;
    }

    List<String> lore = MinecraftUtil.getLore(stack);

    return lore.stream().anyMatch(s -> s.toLowerCase().contains(text.toLowerCase()));
  }

}
