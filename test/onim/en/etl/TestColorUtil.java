package onim.en.etl;


import static org.junit.Assert.*;

import org.junit.Test;

import onim.en.etl.util.ColorUtil;

public class TestColorUtil {

  @Test
  public void testColorApply() {

    float red = 0.1F;
    float green = 0.2F;
    float blue = 0.3F;

    int applied = ColorUtil.applyRed(0xFFFFFFFF, red);
    applied = ColorUtil.applyGreen(applied, green);
    applied = ColorUtil.applyBlue(applied, blue);

    assertTrue(red - ColorUtil.getRed(applied) < 0.01);
    assertTrue(blue - ColorUtil.getBlue(applied) < 0.01);
    assertTrue(green - ColorUtil.getGreen(applied) < 0.01);
    assertTrue(1F - ColorUtil.getAlpha(applied) < 0.01);
  }
}
