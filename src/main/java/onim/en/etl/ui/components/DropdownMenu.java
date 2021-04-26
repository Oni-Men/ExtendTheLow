package onim.en.etl.ui.components;

import net.minecraft.client.Minecraft;
import onim.en.etl.ui.RenderingContext;

public class DropdownMenu extends ListView {

  private int x, y;

  public DropdownMenu(int x, int y, int width, int height) {
    super(width, height);
    this.x = x;
    this.y = y;
  }

  @Override
  public int draw(Minecraft mc) {
    RenderingContext.pop();
    RenderingContext.translate(x, y);
    this.drawGradientRect(x, y, x + width, y + height, 0xAA336633, 0xAA336699);
    super.draw(mc);
    RenderingContext.pop();
    return 0;
  }

}
