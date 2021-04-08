package onim.en.etl.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class RenderCharAtPosEvent extends Event {

  public boolean randomStyle;
  /** Set if the "l" style (bold) is active in currently rendering string */
  public boolean boldStyle;
  /** Set if the "o" style (italic) is active in currently rendering string */
  public boolean italicStyle;
  /** Set if the "n" style (underlined) is active in currently rendering string */
  public boolean underlineStyle;
  /** Set if the "m" style (strikethrough) is active in currently rendering string */
  public boolean strikethroughStyle;

  public RenderCharAtPosEvent(boolean b) {
    this.boldStyle = b;
  }

}
