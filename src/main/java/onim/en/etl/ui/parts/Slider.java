package onim.en.etl.ui.parts;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import onim.en.etl.ui.RenderingContext;

public class Slider extends ActionButton {

  private static ResourceLocation TEX_SLIDER_BUTTON = new ResourceLocation("onim.en.etl:textures/toggle_button.png");

  public Function<Float, String> formatter = null;
  private float sliderValue;
  public boolean dragging;
  private final float min;
  private final float max;
  private final float step;

  private final boolean integer;

  public Slider(int width, String buttonText) {
    this(width, buttonText, 0F, 0F, 1F, 0.01F, false);
  }

  public Slider(int width, String buttonText, boolean integer) {
    this(width, buttonText, 0F, 0F, 1F, 0.01F, integer);
  }

  public Slider(int width, String buttonText, float value, float min, float max, float step,
      boolean integer) {
    super(width, 22, buttonText);
    this.min = min;
    this.max = max;
    this.step = step;
    this.sliderValue = (value - min) / (max - min);
    this.integer = integer;
    this.formatter = (f) -> String.format(this.integer ? "%.0f" : "%.2f", f);
  }

  @Override
  public int draw(Minecraft mc) {
    if (!this.visible) {
      return 0;
    }
    mc.renderEngine.bindTexture(TEX_SLIDER_BUTTON);

    this.hovered = RenderingContext.isHovering(this);

    RenderingContext.push();
    RenderingContext.translate(0, 10);
    RenderingContext ctx = RenderingContext.current;
    this.mouseDragged(mc, ctx.mouseX, ctx.mouseY);


    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

    // bg
    GlStateManager.color(1.0f, 1.0f, 1.0f);
    int k = 4;

    // left edge
    Gui.drawScaledCustomSizeModalRect(ctx.x, ctx.y + 2, 80F, 0F, 32, 64, k, 6, 256F, 64F);
    Gui.drawScaledCustomSizeModalRect(ctx.x + k, ctx.y
        + 2, 112F, 0F, 32, 64, this.width - 2 * k, 6, 256F, 64F);
    // right edge
    Gui.drawScaledCustomSizeModalRect(ctx.x + this.width - k, ctx.y
        + 2, 144F, 0F, 32, 64, k, 6, 256F, 64F);

    // fg
    if (this.dragging) {
      GlStateManager.color(0.3f, 1.0f, 0.3f);
    }
    Gui.drawScaledCustomSizeModalRect((int) (ctx.x
        + width * 0.9 * sliderValue), ctx.y, 0F, 0F, 64, 64, 10, 10, 256F, 64F);

    RenderingContext.pop();

    RenderingContext.push();
    RenderingContext.translate(width / 2, 2);

    if (this.dragging || this.hovered) {
      String s = this.formatter.apply(this.getFloat());
      this.drawTrimedStringWithLeader(s, width, true);
    } else {
      this.drawTrimedStringWithLeader(displayString, width, true);
    }

    if (this.hovered && !this.dragging) {
      this.renderTooltip(displayString, ctx.mouseX, ctx.mouseY);
    }

    RenderingContext.pop();

    return height;
  }

  protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    if (this.visible) {
      if (this.dragging) {
        this.sliderValue = (float) (mouseX - (RenderingContext.current.x)) / (float) (this.width);
        this.roundByStep();
        this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
      }
    }
  }

  @Override
  public void mousePressed(int button) {
    RenderingContext ctx = RenderingContext.current;
    if (button == 0 && ctx.mouseY - ctx.y > 10) {
      this.sliderValue = (float) (ctx.mouseX - (ctx.x)) / (float) (this.width);
      this.roundByStep();
      this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
      this.dragging = true;
    }
  }

  private void roundByStep() {
    float f = this.getFloat() - this.min;
    this.sliderValue = (f - (f % this.step)) / (this.max - this.min);
  }

  @Override
  public void mouseReleased(int button, boolean isInside) {
    this.dragging = false;
    this.onClick();
  }

  public float getFloat() {
    return this.min + (this.max - this.min) * this.sliderValue;
  }

  public int getInt() {
    return (int) this.getFloat();
  }
}
