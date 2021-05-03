package onim.en.etl.extension.normal;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import onim.en.etl.event.BroadcastSoundEvent;
import onim.en.etl.extension.TheLowExtension;

public class MuteDragonDead extends TheLowExtension {
  static final int dragonEndSoundID = 1018;

  @Override
  public String id() {
    return "onim.en.etl.muteDragonDead";
  }

  @Override
  public String category() {
    return "onim.en.etl.category.util";
  }

  @Override
  public void onEnable() {
  }

  @Override
  public void onDisable() {
  }

  @SubscribeEvent
  public void onBroadcastSound(BroadcastSoundEvent event) {
    if (event.id == dragonEndSoundID) {
      event.setCanceled(true);
    }
  }
}
