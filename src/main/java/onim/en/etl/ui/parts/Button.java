package onim.en.etl.ui.parts;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.ui.RenderingContext;

public class Button extends ActionButton {

  private static ResourceLocation TEX_BUTTON = new ResourceLocation("onim.en.etl:textures/button.png");

  public Button(int widthIn, String buttonText) {
    super(widthIn, 12, buttonText);
    this.centered = true;
  }

  @Override
  public int draw(Minecraft mc) {
    if (!this.visible) {
      return 0;
    }
    RenderingContext ctx = RenderingContext.current;
    FontRenderer font = ExtendTheLow.AdvancedFont;
    mc.renderEngine.bindTexture(TEX_BUTTON);

    this.hovered = RenderingContext.isHovering(this);

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
    Gui.drawScaledCustomSizeModalRect(ctx.x, ctx.y, 0F, 0F, 16, 64, i, this.height, 256F, 64F);
    Gui.drawScaledCustomSizeModalRect(ctx.x + i, ctx.y, 64F, 0F, 128, 64, this.width
        - 2 * i, this.height, 256F, 64F);
    // right edge
    Gui.drawScaledCustomSizeModalRect(ctx.x + this.width
        - i, ctx.y, 240F, 0F, 16, 64, i, this.height, 256F, 64F);

    int c = this.enabled ? 0xffffff : 0x666666;

    this.drawCenteredString(font, displayString, ctx.x + this.width / 2, ctx.y + 1, c);

    return this.height;
  }
  

}
