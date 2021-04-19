package onim.en.etl.util;

import net.minecraft.client.renderer.GlStateManager;

public class ColorUtil {

  public static int applyAlpha(int color, float alpha) {
    return color | (((int) (alpha * 255) & 0xFF) << 24);
  }

  public static float getAlpha(int color) {
    return ((color >> 24) & 255) / 255F;
  }

  public static float getRed(int color) {
    return ((color >> 16) & 255) / 255F;
  }

  public static float getGreen(int color) {
    return ((color >> 8) & 255) / 255F;
  }

  public static float getBlue(int color) {
    return ((color) & 255) / 255F;
  }

  public static void glColor(int color) {
    GlStateManager.color(getRed(color), getGreen(color), getBlue(color), getAlpha(color));
  }
}
