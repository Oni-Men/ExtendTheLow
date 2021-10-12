package onim.en.etl.extension.normal;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import onim.en.etl.extension.TheLowExtension;

public class HideNametags extends TheLowExtension {

  @Override
  public String id() {
    return "onim.en.etl.hide-nametags";
  }

  @Override
  public String category() {
    return "onim.en.etl.util";
  }

  @Override
  public void onEnable() {
  }

  @Override
  public void onDisable() {
  }

  @SubscribeEvent
  public void onRenderNametag(RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
    event.setCanceled(true);
  }
  
}
