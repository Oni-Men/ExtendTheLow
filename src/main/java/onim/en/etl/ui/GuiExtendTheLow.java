package onim.en.etl.ui;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.ui.parts.Button;
import onim.en.etl.ui.parts.GuiActionButton;

public class GuiExtendTheLow extends GuiScreen {

  public String title;
  public GuiScreen prevScreen = null;
  private Consumer<List<GuiButton>> initializer = null;
  private Runnable onClose = null;
  private Button done = null;

  public GuiExtendTheLow(String title) {
    this.title = title;
  }

  public GuiExtendTheLow(String title, GuiScreen prevScreen) {
    this.prevScreen = prevScreen;
    this.title = title;
  }

  public void setInitializer(Consumer<List<GuiButton>> initializer) {
    this.initializer = initializer;
  }

  public void setOnClose(Runnable onClose) {
    this.onClose = onClose;
  }

  @Override
  public void initGui() {
    super.initGui();
    if (this.initializer != null) {
      this.initializer.accept(this.buttonList);
    }

    done = new Button(100, "gui.done");
    done.setOnAction(() -> mc.displayGuiScreen(this.prevScreen));
    buttonList.add(done);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    this.drawDefaultBackground();

    int y = 12;

    this.drawCenteredString(ExtendTheLow.AdvancedFont, I18n.format(this.title), width / 2, y,
        0xFFFFFF);
    y += 20;

    for (GuiButton button : buttonList) {
      if (button.equals(done)) {
        done.xPosition = (width - done.width) / 2;
        done.yPosition = height - 32;
        done.drawButton(mc, mouseX, mouseY);
        continue;
      }
      if (button instanceof GuiActionButton) {
        GuiActionButton actionButton = (GuiActionButton) button;
        actionButton.xPosition = width / 2 - 50;
        if (actionButton.centered) {
          actionButton.xPosition = (width - actionButton.width) / 2;
        }
        actionButton.yPosition = y;
        actionButton.drawButton(mc, mouseX, mouseY);
        y += actionButton.height + 8;
      } else {
        button.drawButton(mc, mouseX, mouseY);
      }
    }
  }

  @Override
  protected void actionPerformed(GuiButton button) throws IOException {
    if (button instanceof GuiActionButton) {
      ((GuiActionButton) button).onClick();
    }
  }

  @Override
  public void onGuiClosed() {
    super.onGuiClosed();
    if (this.onClose != null) {
      this.onClose.run();
    }
  }

}
