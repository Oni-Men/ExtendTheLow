package onim.en.etl.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.ui.components.Button;
import onim.en.etl.ui.components.ComponentBase;
import onim.en.etl.util.ColorUtil;
import onim.en.etl.util.Easing;
import onim.en.etl.util.GuiUtil;

public class GuiExtendTheLow extends GuiScreen {

  private List<ComponentBase> components = new ArrayList<>();

  public String title;
  public GuiScreen prevScreen = null;
  private Consumer<List<ComponentBase>> initializer = null;
  private Runnable onClose = null;
  private Button done = null;

  private long frames = 0;

  public GuiExtendTheLow(String title) {
    this.title = title;
  }

  public GuiExtendTheLow(String title, GuiScreen prevScreen) {
    this.prevScreen = prevScreen;
    this.title = title;
  }

  public void setInitializer(Consumer<List<ComponentBase>> initializer) {
    this.initializer = initializer;
  }

  public void setOnClose(Runnable onClose) {
    this.onClose = onClose;
  }

  @Override
  public void initGui() {
    super.initGui();
    components = new ArrayList<>();
    if (this.initializer != null) {
      this.initializer.accept(this.components);
    }

    done = new Button(100, "gui.done");
    done.setOnAction(() -> mc.displayGuiScreen(this.prevScreen));
    components.add(done);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.frames++;
    this.drawDefaultBackground();

    int y = 12;
    float a = this.getGlobalAlpha();
    int color = ColorUtil.applyAlpha(0xFFFFFF, a);

    this.drawCenteredString(ExtendTheLow.AdvancedFont, I18n.format(this.title), width / 2, y, color);
    y += 20;

    GlStateManager.pushMatrix();
    GlStateManager.translate(0, Easing.easeInCubic(1 - a) * height * 0.1, 0);

    RenderingContext.clearStack();
    RenderingContext.current = new RenderingContext(mouseX, mouseY);

    RenderingContext.push();
    RenderingContext.translate(this.width / 2 - 50, 32);

    for (ComponentBase button : components) {
      RenderingContext.translate(0, button.paddingTop);
      button.draw(mc);
      RenderingContext.translate(0, button.height + button.paddingBottom);
    }

    RenderingContext.pop();
    GlStateManager.popMatrix();
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    RenderingContext.current = new RenderingContext(mouseX, mouseY);
    RenderingContext.push();
    RenderingContext.translate(this.width / 2 - 50, 32);

    for (ComponentBase button : components) {
      if (!button.visible)
        continue;
      RenderingContext.translate(0, button.paddingTop);
      if (RenderingContext.isHovering(button)) {
        button.playPressSound(this.mc.getSoundHandler());
        button.mousePressed(mouseButton);
      }

      RenderingContext.translate(0, button.height + button.paddingBottom);
    }
  }

  @Override
  protected void mouseReleased(int mouseX, int mouseY, int state) {
    RenderingContext.current = new RenderingContext(mouseX, mouseY);
    RenderingContext.push();
    RenderingContext.translate(this.width / 2 - 50, 32);
    for (ComponentBase button : components) {
      if (!button.visible)
        continue;

      RenderingContext.translate(0, button.paddingTop);
      button.mouseReleased(state, RenderingContext.isHovering(button));
      RenderingContext.translate(0, button.height + button.paddingBottom);
    }
  }

  @Override
  public void onGuiClosed() {
    super.onGuiClosed();
    if (this.onClose != null) {
      this.onClose.run();
    }
  }

  protected float getGlobalAlpha() {
    return this.frames < 30 ? this.frames / 30F : 1F;
  }

  @Override
  public void drawDefaultBackground() {
    float a = this.getGlobalAlpha();
    int color1 = 0x00336633 | (int) (a * 0xAA) << 24;
    int color2 = 0x00336699 | (int) (a * 0xAA) << 24;
    GuiUtil.drawGradientRectHorizontal(0, 0, width, height, color1, color2);
  }

}
