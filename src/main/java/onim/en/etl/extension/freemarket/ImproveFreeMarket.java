package onim.en.etl.extension.freemarket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.IInventory;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import onim.en.etl.annotation.PrefItem;
import onim.en.etl.extension.TheLowExtension;
import onim.en.etl.ui.custom.GuiFreeMarket;
import onim.en.etl.util.MinecraftUtil;

public class ImproveFreeMarket extends TheLowExtension {

  @PrefItem(id = "onim.en.etl.improveFreeMarket.preventAccidentalPurchases", type = boolean.class)
  public boolean preventAccidentalPurchases = true;

  @PrefItem(id = "onim.en.etl.improveFreeMarket.improveBackgroundRender", type = boolean.class)
  public boolean improveBackgroundRender = true;

  @Override
  public String id() {
    return "onim.en.etl.improveFreeMarket";
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
    GuiFreeMarket guiFreeMarket = new GuiFreeMarket(this, mc.thePlayer.inventory, inv);

    event.gui = guiFreeMarket;
  }

}
