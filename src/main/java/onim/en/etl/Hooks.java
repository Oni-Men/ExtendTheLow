package onim.en.etl;

import net.minecraftforge.common.MinecraftForge;
import onim.en.etl.event.GetCharWidthEvent;
import onim.en.etl.event.RenderCharAtPosEvent;

public class Hooks {

  public static GetCharWidthEvent onGetCharWidth(char ch) {
    GetCharWidthEvent event = new GetCharWidthEvent(ch);
    MinecraftForge.EVENT_BUS.post(event);
    return event;
  }

  public static void onRenderCharAtPos(boolean boldStyle) {
    MinecraftForge.EVENT_BUS.post(new RenderCharAtPosEvent(boldStyle));
  }

}
