package onim.en.etl.ui.custom;

import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;
import onim.en.etl.util.TheLowUtil;

public class GuiEquipMagicOre extends GuiMerchant {

  public GuiEquipMagicOre(InventoryPlayer inventoryPlayer, IMerchant merchant, World worldIn) {
    super(inventoryPlayer, merchant, worldIn);
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    try {
      super.drawScreen(mouseX, mouseY, partialTicks);
    } catch (IndexOutOfBoundsException e) {
      if (TheLowUtil.isPlayingTheLow()) {
        return;
      }
      e.printStackTrace();
    }
  }
}
