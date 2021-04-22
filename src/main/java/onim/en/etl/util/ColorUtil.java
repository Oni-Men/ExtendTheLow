package onim.en.etl.util;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;

public class ColorUtil {

  public static int applyAlpha(int color, float alpha) {
    color = reset(color, 0x00FFFFFF);
    return color | (((int) (alpha * 255) & 0xFF) << 24);
  }

  public static int applyRed(int color, float red) {
    color = reset(color, 0xFF00FFFF);
    red = MathHelper.clamp_float(red, 0F, 1F);
    return color | (((int) (red * 255)) << 16);
  }

  public static int applyGreen(int color, float green) {
    color = reset(color, 0xFFFF00FF);
    green = MathHelper.clamp_float(green, 0F, 1F);
    return color | (((int) (green * 255)) << 8);
  }

  public static int applyBlue(int color, float blue) {
    color = reset(color, 0xFFFFFF00);
    blue = MathHelper.clamp_float(blue, 0F, 1F);
    return color | (((int) (blue * 255)));
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

  public static int reset(int color, int mask) {
    return color & mask;
  }

  public static int scale(int color, float s) {
    float red = getRed(color) * s;
    float green = getGreen(color) * s;
    float blue = getBlue(color) * s;
    return toColorInt(red, green, blue);
  }

  public static int toColorInt(float red, float green, float blue) {
    int color = 0xFFFFFFFF;
    color = applyRed(color, red);
    color = applyGreen(color, green);
    color = applyBlue(color, blue);
    return color;
  }

  public static void glColor(int color) {
    GlStateManager.color(getRed(color), getGreen(color), getBlue(color), getAlpha(color));
  }
}
