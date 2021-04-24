package onim.en.etl.ui.custom;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import onim.en.etl.extension.freemarket.ImproveFreeMarket;
import onim.en.etl.extension.freemarket.PreventPurchase;
import onim.en.etl.extension.freemarket.PreventPurchase.ClickFreeMarketItem;
import onim.en.etl.util.GuiUtil;

public class GuiFreeMarket extends GuiChest {

  private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
  private int inventoryRows;

  public ImproveFreeMarket extension;

  public GuiFreeMarket(ImproveFreeMarket extension, IInventory playerInv, IInventory chestInv) {
    super(playerInv, chestInv);
    this.extension = extension;
    this.inventoryRows = chestInv.getSizeInventory() / 9;
  }

  public void initGui() {
    super.initGui();
    PreventPurchase.reset();
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
  }

}
