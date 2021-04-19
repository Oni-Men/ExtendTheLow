package onim.en.etl.ui.parts;

import net.minecraft.client.gui.GuiTextField;
import onim.en.etl.ExtendTheLow;

public class TextField extends GuiTextField {

  private static int nextId = 10000;

  public static int getNextID() {
    return nextId++;
  }

  public TextField(int x, int y, int width) {
    super(getNextID(), ExtendTheLow.AdvancedFont, x, y, width, 10);
  }

  @Override
  public void drawTextBox() {
    // TODO Auto-generated method stub
    super.drawTextBox();
  }
}
