package onim.en.etl.event;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;

public class GuiRenderItemEvent extends Event {

  private ItemStack stack;
  private int x;
  private int y;

  public GuiRenderItemEvent(ItemStack stack, int x, int y) {
    this.stack = stack;
    this.x = x;
    this.y = y;
  }
  
  public ItemStack getItemStack() {
    return this.stack;
  }
  
  public int getX() {
    return x;
  }
  
  public int getY() {
    return y;
  }
  
  @Override
  public boolean isCancelable() {
    return false;
  }

}
