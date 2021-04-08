package onim.en.etl.util;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class FontUtil {

  private static final BufferedImage Dummy = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
  private static final Graphics2D G = Dummy.createGraphics();

  public static Font findFontCanDisplayUp(List<Font> fonts, char ch) {
    Iterator<Font> iterator = fonts.iterator();
    while (iterator.hasNext()) {
      Font next = iterator.next();
      if (next.canDisplay(ch))
        return next;
    }
    return null;
  }

  public static boolean canDisplay(Font font, char ch) {
    return font.canDisplay(ch);
  }

  public static float getStringWidthByFont(Font font, String text) {
    return G.getFontMetrics(font).stringWidth(text);
  }

  public static float getCharWidthByFont(Font font, char ch) {
    return G.getFontMetrics(font).charWidth(ch);
  }

  public static Font loadFont(ResourceLocation location) {
    Minecraft mc = Minecraft.getMinecraft();
    try {
      InputStream fontIn = mc.getResourceManager().getResource(location).getInputStream();
      return Font.createFont(Font.TRUETYPE_FONT, fontIn);
    } catch (IOException | FontFormatException e) {
      e.printStackTrace();
    }
    return null;
  }

}
