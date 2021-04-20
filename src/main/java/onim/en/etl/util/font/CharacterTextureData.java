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

public class CharacterTextureData extends FontData {

  private static final Vector2f ZERO_VEC = new Vector2f(0, 0);

  private float glyphWidth = 0F;
  private float baseline = 0F;
  
  public CharacterTextureData(List<Font> fonts, char ch, int scale) {
    this.scale = scale;
    FontGenerateWorker.addGenerateTask(() -> {
      BufferedImage generated = this.generateChar(fonts, ch, scale);
      return new FontGenerateData(this, generated);
    });
  }

  public void complete() {
    this.isInitialized = true;
  }

  public BufferedImage generateChar(List<Font> fonts, char ch, int scale) {
    Font font = FontUtil.findFontCanDisplayUp(fonts, ch);
    if (font == null) {
      glyphWidth = -1;
      width = 1;
      height = 1;
      return new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
    }
    width = (int) FontUtil.getCharWidthByFont(font, ch);
    height = 16 * scale;

    BufferedImage image = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = (Graphics2D) image.getGraphics();

    g.setFont(font);
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
        RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

    FontMetrics fm = g.getFontMetrics();
    Rectangle2D bounds = fm.getStringBounds(Character.toString(ch), g);

    float x = (float) bounds.getX();
    float y = fm.getHeight() - fm.getLeading() - fm.getDescent();

    g.drawString(String.valueOf(ch), x, y);

    // g.translate(0, scale * 8);
    // g.scale(0.2, 0.2);
    // g.setColor(Color.gray);
    // g.drawString(String.format("%.1f", bounds.getWidth()), 0, 0);

    this.baseline = fm.getLeading() + fm.getDescent();
    this.glyphWidth = (float) (bounds.getWidth() / scale);

    return image;
  }

  @Override
  public float getCharWidth(char ch) {
    return this.glyphWidth;
  }

  @Override
  public float getCharHeight() {
    return 16F;
  }

  @Override
  public float getBaseline(char ch) {
    return this.baseline;
  }

  @Override
  public Vector2f getUVCoord(char ch) {
    return ZERO_VEC;
  }

  public static class CharacterGenerateData {

    public CharacterTextureData data;
    public BufferedImage generated;

    public CharacterGenerateData(CharacterTextureData data, BufferedImage generated) {
      this.data = data;
      this.generated = generated;
    }

  }

}
