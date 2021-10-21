package onim.en.etl.extension.normal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import onim.en.etl.event.GuiRenderItemEvent;
import onim.en.etl.extension.TheLowExtension;

public class NoThrowMarker extends TheLowExtension {

  private static final ResourceLocation NO_TRASH_ICON = new ResourceLocation("onim.en.etl:textures/no-trash.png");
  
  @Override
  public String id() {
    return "onim.en.etl.noThrowMarker";
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
  public void onRenderItem(GuiRenderItemEvent event) {
    GlStateManager.pushMatrix();

    Minecraft mc = Minecraft.getMinecraft();

    if (isThrowable(event.getItemStack())) {
      mc.getTextureManager().bindTexture(NO_TRASH_ICON);

      GlStateManager.enableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.disableLighting();
      GlStateManager.translate(0, 0, 500);
      
      int tx = 0;
      int ty = 0;
      int size = 16;
      
      float alpha = 0.33f;
      
      // If the GUI scale were Large or Auto, display icon at right-bottom of slot and make it smaller
      if (mc.gameSettings.guiScale % 3 == 0) {
        tx = 8;
        ty = 8;
        size = 8;
        alpha = 1.0F;
      }

      GlStateManager.color(1f, 1f, 1f, alpha);
      
      Gui.drawScaledCustomSizeModalRect(event.getX() + tx, event.getY() + ty, 0f, 0f, 64, 64, size, size, 64F, 64F);
      
      GlStateManager.disableBlend();
      GlStateManager.enableAlpha();
      GlStateManager.enableLighting();
    }

    GlStateManager.popMatrix();

  }

  private boolean isThrowable(ItemStack stack) {
    if (stack == null) {
      return false;
    }

    NBTTagCompound compound = stack.getTagCompound();

    if (compound == null) {
      return false;
    }

    String value = compound.getString("no_throw_item");
    return !value.isEmpty();
  }
}
