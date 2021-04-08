package onim.en.etl.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class GetCharWidthEvent extends Event {

  private char ch;
  private float width;

  public GetCharWidthEvent(char ch) {
    this.ch = ch;
    this.width = 0;
  }

  @Override
  public boolean isCancelable() {
    return true;
  }

  public char getChar() {
    return this.ch;
  }

  public void setWidth(float width) {
    this.width = width;
  }

  public float getWidth() {
    return this.width;
  }
}
