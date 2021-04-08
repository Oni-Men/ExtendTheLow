package onim.en.etl.util.font;

import java.awt.image.BufferedImage;

public class FontGenerateData {
  public FontData data;
  public BufferedImage image;

  public FontGenerateData(FontData data, BufferedImage image) {
    this.data = data;
    this.image = image;
  }
}