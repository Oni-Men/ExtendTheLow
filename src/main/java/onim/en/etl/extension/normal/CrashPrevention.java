package onim.en.etl.extension.normal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import onim.en.etl.extension.TheLowExtension;
import onim.en.etl.ui.custom.GuiEquipMagicOre;

public class CrashPrevention extends TheLowExtension {

  private static final String MERCHANT_NAME = "魔法石装着";

  @Override
  public String id() {
    return "crashPrevention";
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
  public void onInitGui(InitGuiEvent event) {
    if (event.gui instanceof GuiEquipMagicOre) {
      return;
    }

    if (!(event.gui instanceof GuiMerchant)) {
      return;
    }

    GuiMerchant gui = (GuiMerchant) event.gui;
    IChatComponent displayName = gui.getMerchant().getDisplayName();
    
    if (!displayName.getUnformattedText().equals(MERCHANT_NAME)) {
      return;
    }

    Minecraft mc = Minecraft.getMinecraft();
    GuiEquipMagicOre equipMagicOre =
        new GuiEquipMagicOre(mc.thePlayer.inventory, gui.getMerchant(), mc.theWorld);

    mc.displayGuiScreen(equipMagicOre);
  }

}
