package onim.en.etl.ui.parts;

import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import onim.en.etl.ExtendTheLow;

public class ToggleButton extends GuiActionButton {

  private static ResourceLocation TEX_TOGGLE_BUTTON =
      new ResourceLocation("onim.en.etl:textures/toggle_button.png");

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
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    if (this.visible) {
      FontRenderer font = ExtendTheLow.RenderFont;
      mc.renderEngine.bindTexture(TEX_TOGGLE_BUTTON);

      int i = font.getStringWidth(this.displayString);
      boolean xInside = mouseX >= this.xPosition && mouseX < this.xPosition + this.width + i;
      boolean yInside = mouseY >= this.yPosition && mouseY < this.yPosition + this.height;
      this.hovered = xInside && yInside;

      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

      // bg
      GlStateManager.color(1.0f, 1.0f, 1.0f);
      Gui.drawScaledCustomSizeModalRect(this.xPosition, this.yPosition, 64F, 0F, 128, 64, 20, 10,
          256F,
          64F);

      // fg
      if (this.toggleState) {
        GlStateManager.color(0.3f, 1.0f, 0.3f);
      }
      Gui.drawScaledCustomSizeModalRect(this.xPosition + this.getToggleStatePosition(),
          this.yPosition, 0F,
          0F, 64, 64, 10, 10, 256F, 64F);

      this.mouseDragged(mc, mouseX, mouseY);

      this.drawString(font, displayString, this.xPosition + 22, this.yPosition + 1, 0xffffff);
    }
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

    float translate = (elapsed / 10F);
    if (elapsed > 100) {
      translate = 10F;
    }

    if (this.toggleState) {
      return (int) translate;
    } else {
      return (int) (10 - translate);
    }
  }

}
