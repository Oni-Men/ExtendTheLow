package onim.en.etl.event;

import net.minecraftforge.fml.common.eventhandler.Event;

public class BroadcastSoundEvent extends Event {

  public int id;

  public BroadcastSoundEvent(int id) {
    this.id = id;
  }

  @Override
  public boolean isCancelable() {
    return true;
  }
}
