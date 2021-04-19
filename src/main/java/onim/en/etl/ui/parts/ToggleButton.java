package onim.en.etl.ui.parts;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.ui.RenderingContext;
import onim.en.etl.util.Easing;

public class ToggleButton extends ActionButton {

  private static ResourceLocation TEX_TOGGLE_BUTTON = new ResourceLocation("onim.en.etl:textures/toggle_button.png");

  private boolean toggleState = false;
  private long toggleStateChagned = 0;

  private Consumer<Boolean> atClicked;

  public ToggleButton(String buttonText) {
    this(buttonText, false);
  }

  public ToggleButton(String buttonText, boolean state) {
    this(buttonText, state, null);
  }

  public ToggleButton(String buttonText, boolean state, Consumer<Boolean> onclick) {
    super(20, 10, buttonText);
    this.toggleState = state;
    this.atClicked = onclick;
  }


  public void setState(boolean b) {
    this.toggleState = b;
    this.toggleStateChagned = Minecraft.getSystemTime();
  }

  public void toggleState() {
    this.setState(!this.toggleState);
  }

  public boolean getState() {
    return this.toggleState;
  }

  @Override
  public int draw(Minecraft mc) {
    if (!this.visible) {
      return 0;
    }
    FontRenderer font = ExtendTheLow.AdvancedFont;
    mc.renderEngine.bindTexture(TEX_TOGGLE_BUTTON);

    int i = font.getStringWidth(this.displayString);
    this.width = 22 + i;

    this.hovered = RenderingContext.isHovering(this);
    RenderingContext ctx = RenderingContext.current;

    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

    // bg
    GlStateManager.color(1.0f, 1.0f, 1.0f);
    Gui.drawScaledCustomSizeModalRect(ctx.x, ctx.y, 64F, 0F, 128, 64, 20, 10, 256F, 64F);

    // fg
    if (this.toggleState) {
      GlStateManager.color(0.3f, 1.0f, 0.3f);
    }
    Gui.drawScaledCustomSizeModalRect(ctx.x + this.getToggleStatePosition(), ctx.y, 0F, 0F, 64, 64, 10, 10, 256F, 64F);


    this.drawString(font, displayString, ctx.x + 22, ctx.y + 1, 0xffffff);
    return height;
  }

  @Override
  public void onClick() {
    this.toggleState();
    super.onClick();

    if (this.atClicked != null) {
      this.atClicked.accept(this.toggleState);
    }
  }

  private int getToggleStatePosition() {
    if (!Minecraft.isFancyGraphicsEnabled()) {
      return this.toggleState ? 10 : 0;
    }
    long elapsed = Minecraft.getSystemTime() - this.toggleStateChagned;

    float translate = Easing.easeOutCubic(elapsed / 100F) * 10F;

    if (this.toggleState) {
      return (int) translate;
    } else {
      return (int) (10 - translate);
    }
  }

}
