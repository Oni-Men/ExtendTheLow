package onim.en.etl.ui.custom;

import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

public class GuiEquipMagicOre extends GuiMerchant {

  public GuiEquipMagicOre(InventoryPlayer inventoryPlayer, IMerchant merchant, World worldIn) {
    super(inventoryPlayer, merchant, worldIn);
  }

  public void updateScreen() {}

}
