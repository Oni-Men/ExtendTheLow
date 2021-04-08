package onim.en.etl.ui.parts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import onim.en.etl.ExtendTheLow;

public class Button extends GuiActionButton {

  private static ResourceLocation TEX_BUTTON =
      new ResourceLocation("onim.en.etl:textures/button.png");

  public Button(int widthIn, String buttonText) {
    super(widthIn, 12, buttonText);
    this.centered = true;
  }

  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    if (this.visible) {
      FontRenderer font = ExtendTheLow.RenderFont;
      mc.renderEngine.bindTexture(TEX_BUTTON);

      boolean xInside = mouseX >= this.xPosition && mouseX < this.xPosition + this.width;
      boolean yInside = mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
      this.hovered = xInside && yInside;

      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

      // bg
      GlStateManager.color(0.3f, 0.7f, 0.3f);
      if (this.hovered) {
        GlStateManager.color(0.3f, 0.8f, 0.3f);
      }

      if (!this.enabled) {
        GlStateManager.color(0.3f, 0.3f, 0.3f);
      }

      int i = 4;

      // left edge
      Gui.drawScaledCustomSizeModalRect(this.xPosition, this.yPosition, 0F, 0F, 16, 64, i,
          this.height, 256F, 64F);
      Gui.drawScaledCustomSizeModalRect(this.xPosition + i, this.yPosition, 64F, 0F, 128, 64,
          this.width - 2 * i, this.height, 256F, 64F);
      // right edge
      Gui.drawScaledCustomSizeModalRect(this.xPosition + this.width - i, this.yPosition, 240F, 0F,
          16, 64, i, this.height, 256F, 64F);

      int c = this.enabled ? 0xffffff : 0x666666;

      this.mouseDragged(mc, mouseX, mouseY);
      this.drawCenteredString(font, displayString, this.xPosition + this.width / 2,
          this.yPosition + 1, c);
    }
  }

}
