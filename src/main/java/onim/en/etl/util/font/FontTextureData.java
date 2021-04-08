package onim.en.etl.util.font;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import onim.en.etl.util.FontUtil;

public class FontTextureData extends FontData {

  public FontTextureData(List<Font> fonts, int page, int scale) {
    this.scale = scale;
    this.glyphWidth = new float[256];
    this.baselines = new float[256];
    this.uvCoords = new Vector2f[256];
    FontGenerateWorker.addGenerateTask(() -> {
      BufferedImage generated = this.generate(fonts, page, scale);
      return new FontGenerateData(this, generated);
    });
  }

  public void complete() {
    this.isInitialized = true;
  }

  public BufferedImage generate(List<Font> fonts, int page, int scale) {
    width = 256 * scale;
    height = 256 * scale;

    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) image.getGraphics();

    int offset = page * 256;
    for (int y = 0; y < 16; y++) {
      for (int x = 0; x < 16; x++) {
        char codePoint = (char) (offset + y * 16 + x);

        float u = (float) (x * 16 * scale) / (float) width;
        float v = (float) (y * 16 * scale) / (float) height;

        g.drawImage(generateChar(fonts, codePoint, scale), (int) (u * width), (int) (v * height), null);
      }
    }
    return image;
  }

  public boolean isInitialized() {
    return this.isInitialized;
  }

  public BufferedImage generateChar(List<Font> fonts, char ch, int scale) {
    BufferedImage image = new BufferedImage(16 * scale, 16 * scale, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) image.getGraphics();
    g.setFont(new Font("System", Font.PLAIN, 12 * scale));

    Font font = FontUtil.findFontCanDisplayUp(fonts, ch);
    this.baselines[ch % 256] = 0;
    if (font == null) {
      this.glyphWidth[ch % 256] = -1;
      return image;
    }
    g.setFont(font);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    FontMetrics fm = g.getFontMetrics();

    float x = 0F;
    float y = fm.getHeight() - fm.getLeading() - fm.getDescent();

    g.drawString(String.valueOf(ch), x, y);
    g.drawString(String.valueOf(ch), x, y);

    Rectangle2D bounds = fm.getStringBounds(Character.toString(ch), g);

    float u = ((ch % 256) % 16) * 16 * scale;
    float v = ((ch % 256) / 16) * 16 * scale;
    this.uvCoords[ch % 256] = new Vector2f(u / (float) width, v / (float) height);
    this.baselines[ch % 256] = fm.getLeading() + fm.getDescent();
    this.glyphWidth[ch % 256] = (float) (bounds.getWidth() / scale);

    return image;
  }

}
