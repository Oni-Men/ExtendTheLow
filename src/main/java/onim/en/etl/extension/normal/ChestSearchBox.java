package onim.en.etl.extension.normal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import onim.en.etl.extension.TheLowExtension;
import onim.en.etl.ui.custom.GuiSearchChest;
import onim.en.etl.util.MinecraftUtil;

public class ChestSearchBox extends TheLowExtension {

  @Override
  public String id() {
    return "onim.en.etl.chestSearchBox";
  }

  @Override
  public String category() {
    return "onim.en.etl.category.util";
  }

  @Override
  public void onEnable() {}

  @Override
  public void onDisable() {}

  @SubscribeEvent
  public void onGuiOpen(GuiOpenEvent event) {
    if (!(event.gui instanceof GuiChest)) {
      return;
    }

    GuiChest gui = (GuiChest) event.gui;
    IInventory inv = MinecraftUtil.getChestInventory(gui);

    if (inv == null) {
      return;
    }

    Minecraft mc = Minecraft.getMinecraft();
    GuiSearchChest chest = new GuiSearchChest(mc.thePlayer.inventory, inv);

    event.gui = chest;
  }

}
