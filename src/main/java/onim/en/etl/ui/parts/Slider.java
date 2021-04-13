package onim.en.etl.ui.parts;

import java.util.function.Function;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import onim.en.etl.ExtendTheLow;

public class Slider extends GuiActionButton {

  private static ResourceLocation TEX_SLIDER_BUTTON =
      new ResourceLocation("onim.en.etl:textures/toggle_button.png");

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
    super(width, 12, buttonText);
    this.id = ButtonIDRegistry.nextButton(this);
    this.min = min;
    this.max = max;
    this.step = step;
    this.sliderValue = (value - min) / (max - min);
    this.integer = integer;
    this.formatter = (f) -> String.format(this.integer ? "%.0f" : "%.2f", f);
  }

  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    if (this.visible) {
      FontRenderer font = ExtendTheLow.AdvancedFont;
      mc.renderEngine.bindTexture(TEX_SLIDER_BUTTON);

      int i = font.getStringWidth(this.displayString);
      boolean xInside = mouseX >= this.xPosition && mouseX < this.xPosition + this.width + i;
      boolean yInside = mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
      this.hovered = xInside && yInside;

      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

      // bg
      GlStateManager.color(1.0f, 1.0f, 1.0f);
      int k = 4;

      // left edge
      Gui.drawScaledCustomSizeModalRect(this.xPosition, this.yPosition + 2, 80F, 0F, 32, 64, k,
          this.height / 2, 256F, 64F);
      Gui.drawScaledCustomSizeModalRect(this.xPosition + k, this.yPosition + 2, 112F, 0F, 32, 64,
          this.width - 2 * k, this.height / 2, 256F, 64F);
      // right edge
      Gui.drawScaledCustomSizeModalRect(this.xPosition + this.width - k, this.yPosition + 2, 144F,
          0F, 32, 64, k, this.height / 2, 256F, 64F);

      // fg
      if (this.dragging) {
        GlStateManager.color(0.3f, 1.0f, 0.3f);
      }
      Gui.drawScaledCustomSizeModalRect((int) (this.xPosition + width * 0.9 * sliderValue),
          this.yPosition, 0F, 0F, 64, 64, 10, 10, 256F, 64F);

      this.mouseDragged(mc, mouseX, mouseY);

      if (this.dragging || this.hovered) {
        this.drawCenteredString(font, this.formatter.apply(this.getFloat()),
            this.xPosition + this.width / 2, this.yPosition + 1, 0xffffff);
      } else {
        this.drawCenteredString(font, displayString, this.xPosition + this.width / 2,
            this.yPosition + 1, 0xffffff);
      }
    }
  }

  @Override
  protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
    if (this.visible) {
      if (this.dragging) {
        this.sliderValue = (float) (mouseX - (this.xPosition)) / (float) (this.width);
        this.roundByStep();
        this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
      }
    }
  }

  @Override
  public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
    if (super.mousePressed(mc, mouseX, mouseY)) {
      this.sliderValue = (float) (mouseX - (this.xPosition)) / (float) (this.width);
      this.roundByStep();
      this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
      this.dragging = true;
      return true;
    } else {
      return false;
    }
  }
  
  private void roundByStep() {
    float f = this.getFloat() - this.min;
    this.sliderValue = (f - (f % this.step)) / (this.max - this.min);
  }

  @Override
  public void mouseReleased(int mouseX, int mouseY) {
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
