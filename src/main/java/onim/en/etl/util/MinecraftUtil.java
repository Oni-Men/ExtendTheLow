package onim.en.etl.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants.NBT;

public class MinecraftUtil {

  public static IInventory getChestInventory(GuiChest chest) {
    for (Field field : chest.getClass().getDeclaredFields()) {
      try {

        int mod = field.getModifiers();

        if (!Modifier.isPrivate(mod)) {
          continue;
        }

        if (!IInventory.class.isAssignableFrom(field.getType())) {
          continue;
        }

        field.setAccessible(true);
        IInventory inv = (IInventory) field.get(chest);

        if (inv instanceof InventoryPlayer) {
          continue;
        }

        return inv;
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  public static List<String> getLore(ItemStack stack) {
    ArrayList<String> list = new ArrayList<String>();
    if (stack == null)
      return list;

    NBTTagCompound root = stack.getTagCompound();

    if (root == null)
      return list;

    NBTTagCompound display = root.getCompoundTag("display");

    if (display == null)
      return list;

    NBTTagList lore = display.getTagList("Lore", NBT.TAG_STRING);

    if (lore == null)
      return list;


    for (int i = 0; i < lore.tagCount(); i++) {
      NBTBase tag = lore.get(i);
      list.add(tag.toString());
    }

    return list;
  }

  public static NBTTagCompound getNBTTagCompound(ItemStack stack, String namespace) {
    if (stack == null) {
      return null;
    }

    NBTTagCompound root = stack.getTagCompound();

    if (root == null)
      return null;
    
    if (namespace.isEmpty()) {
      return root;
    }

    String[] split = namespace.split("\\.");
    
    if (split.length == 0) {
      return root.getCompoundTag(namespace);
    }

    NBTTagCompound tagCompound = root;
    for (int i = 0; i < split.length; i++) {
      tagCompound = tagCompound.getCompoundTag(split[i]);
    }

    return tagCompound;
  }

}
