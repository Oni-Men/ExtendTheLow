package onim.en.etl.extension.freemarket;

import javax.annotation.Nullable;

import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PreventPurchase {

  private static ClickFreeMarketItem lastClick = null;

  public static void update() {
    if (lastClick == null) {
      return;
    }

    if (System.currentTimeMillis() - lastClick.clickedAt > 5000) {
      lastClick = null;
    }
  }

  public static void reset() {
    lastClick = null;
  }

  public static boolean isFreeMarketItem(@Nullable ItemStack stack) {
    if (stack == null) {
      return false;
    }
    NBTTagCompound root = stack.getTagCompound();
    if (root == null) {
      return false;
    }

    return root.hasKey("fm_show_price") && root.hasKey("fm_show_hashcode");
  }

  public static void clickFreeMarketItem(Slot slot) {
    if (lastClick != null) {
      if (lastClick.slot.equals(slot)) {
        lastClick = null;
        return;
      }
    }
    lastClick = new ClickFreeMarketItem(slot);
  }

  public static boolean canPurchase(Slot slot) {
    if (lastClick == null) {
      return true;
    }
    return false;
  }

  public static ClickFreeMarketItem getFreeMarketClickInfo() {
    return lastClick;
  }

  public static class ClickFreeMarketItem {
    public long clickedAt;
    public Slot slot;

    public ClickFreeMarketItem(Slot slot) {
      this.slot = slot;
      this.clickedAt = System.currentTimeMillis();
    }
  }
}
