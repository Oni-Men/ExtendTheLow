package onim.en.etl.util;

import net.minecraft.util.MathHelper;

public class Easing {

  public static float easeInCubic(float x) {
    x = MathHelper.clamp_float(x, 0, 1);
    return x * x * x;
  }

  public static float easeOutCubic(float x) {
    x = 1 - MathHelper.clamp_float(x, 0, 1);
    return 1 - (x * x * x);
  }

}
