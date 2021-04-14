package onim.en.etl.ui;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import onim.en.etl.Prefs;
import onim.en.etl.event.RenderCharAtPosEvent;
import onim.en.etl.util.FontUtil;
import onim.en.etl.util.font.CharacterTextureData;
import onim.en.etl.util.font.FontData;
import onim.en.etl.util.font.FontTextureData;

public class AdvancedFontRenderer extends FontRenderer implements IResourceManagerReloadListener {

  public static Font Ubuntu, UbuntuBold, Sawarabi;

  public static boolean bigMode = false;
  public static boolean boldMode = false;
  private static FontTextureData[] fontDatas = new FontTextureData[256];
  private static FontTextureData[] boldDatas = new FontTextureData[256];
  private static CharacterTextureData[] bigFontDatas = new CharacterTextureData[256 * 256];
  private static CharacterTextureData[] bigFontBoldDatas = new CharacterTextureData[256 * 256];
  private static List<Font> fonts = new ArrayList<>();
  private int prevScaleFactor = -1;

  public RenderCharAtPosEvent lastRenderCharAtPosEvent = null;

  public static List<String> getFontNameList() {
    return new ArrayList<>();
  }

  public static void resetFontTexture() {
    if (fontDatas != null) {
      for (FontTextureData data : fontDatas) {
        if (data != null)
          data.destroy();
      }
      for (FontTextureData data : boldDatas) {
        if (data != null)
          data.destroy();
      }
      for (CharacterTextureData data : bigFontDatas) {
        if (data != null)
          data.destroy();
      }
      for (CharacterTextureData data : bigFontBoldDatas) {
        if (data != null)
          data.destroy();
      }

      fontDatas = new FontTextureData[256];
      boldDatas = new FontTextureData[256];
      bigFontDatas = new CharacterTextureData[256 * 256];
      bigFontBoldDatas = new CharacterTextureData[256 * 256];
    }
  }

  public AdvancedFontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn) {
    super(gameSettingsIn, location, textureManagerIn, false);

    Ubuntu = FontUtil.loadFont(new ResourceLocation("onim.en.etl:font/Ubuntu-R.ttf"));
    UbuntuBold = FontUtil.loadFont(new ResourceLocation("onim.en.etl:font/Ubuntu-B.ttf"));
    Sawarabi = FontUtil.loadFont(new ResourceLocation("onim.en.etl:font/SawarabiGothic-Regular.ttf"));

    fonts = Arrays.asList(Ubuntu, Sawarabi);
  }

  @Override
  public int drawString(String text, float x, float y, int color, boolean dropShadow) {
    if (!Prefs.get().betterFont) {
      return super.drawString(text, x, y, color, dropShadow);
    }

    return super.drawString(text, x, y, color, dropShadow);
  }

  @Override
  public void onResourceManagerReload(IResourceManager resourceManager) {
    super.onResourceManagerReload(resourceManager);
  }

  @Override
  protected float renderUnicodeChar(char ch, boolean italic) {
    if (!Prefs.get().betterFont) {
      return super.renderUnicodeChar(ch, italic);
    }

    int scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    if (prevScaleFactor != scaleFactor) {
      prevScaleFactor = scaleFactor;
      resetFontTexture();
    }

    FontData data = this.getFontTextureData(ch, scaleFactor);

    if (!data.isInitialized()) {
      return 0F;
    }

    float w = data.getTextureWidth();
    float h = data.getTextureHeight();

    float charWidth = data.getCharWidth(ch);

    if (charWidth == -1) {
      return super.renderUnicodeChar(ch, italic);
    }

    float texCharWidth = charWidth * data.getScale() / w;
    float texCharHeight = data.getCharHeight() * data.getScale() / h;

    Vector2f uv = data.getUVCoord(ch);

    GlStateManager.bindTexture(data.getGlTextureId());
    GlStateManager.enableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);

    switch (ch) {
      case 160:
        return 4F;
      case 167:
        return -1F;
      case ' ':
        return 4F;
    }

    float k = italic ? 1.2F : 0.0F;

    GlStateManager.pushMatrix();
    if (FontUtil.canDisplay(Ubuntu, ch)) {
      GlStateManager.translate(0, 1F, 0);
    }

    GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

    GL11.glTexCoord2f(uv.x, uv.y);
    GL11.glVertex3f(this.posX + k, this.posY, 0F);

    GL11.glTexCoord2f(uv.x, uv.y + texCharHeight);
    GL11.glVertex3f(this.posX - k, this.posY + 7.99F, 0F);

    GL11.glTexCoord2f(uv.x + texCharWidth, uv.y);
    GL11.glVertex3f(this.posX + charWidth / 2F + k, this.posY, 0F);

    GL11.glTexCoord2f(uv.x + texCharWidth, uv.y + texCharHeight);
    GL11.glVertex3f(this.posX + charWidth / 2F - k, this.posY + 7.99F, 0F);

    GL11.glEnd();

    GlStateManager.popMatrix();
    GlStateManager.enableAlpha();
    return charWidth / 2F + 1F;
  }

  @Override
  protected float renderDefaultChar(int ch, boolean italic) {
    if (!Prefs.get().betterFont) {
      return super.renderDefaultChar(ch, italic);
    }
    return this.renderUnicodeChar((char) ch, italic);
  }

  @Override
  public int getCharWidth(char ch) {
    if (!Prefs.get().betterFont) {
      return super.getCharWidth(ch);
    }
    return (int) (this.getCharWidthFloat_NonOptifine(ch));
  }

  public float getCharWidthFloat_NonOptifine(char ch) {
    switch (ch) {
      case 160:
        return 4F;
      case 167:
        return -1F;
      case ' ':
        return 4F;
    }

    int scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    if (prevScaleFactor != scaleFactor) {
      prevScaleFactor = scaleFactor;
    }

    FontData data = this.getFontTextureData(ch, scaleFactor);
    if (!data.isInitialized()) {
      return 0F;
    }

    float charWidth = data.getCharWidth(ch);
    if (charWidth == -1) {
      return super.getCharWidth(ch);
    }

    return charWidth / 2F + 1F;
  }

  public int getStringWidth(String text) {
    if (!Prefs.get().betterFont) {
      return super.getStringWidth(text);
    }
    if (text == null) {
      return 0;
    } else {
      double sum = text.chars().mapToDouble(i -> this.getCharWidth((char) i)).sum();
      return (int) sum;
    }
  }

  private List<Font> deriveFonts(float scale) {
    return this.deriveFonts(scale, Font.PLAIN);
  }

  private List<Font> deriveFonts(float scale, int style) {
    return fonts.stream().filter(f -> f != null).map(f -> {
      if (f.getFamily().equals("Ubuntu") && style == Font.BOLD) {
        return UbuntuBold.deriveFont(scale);
      }
      return f.deriveFont(style, scale);
    }).collect(Collectors.toList());
  }

  private FontData getFontTextureData(char ch, int scaleFactor) {
    int p = ch / 256;
    if (bigMode && Minecraft.isFancyGraphicsEnabled()) {
      CharacterTextureData[] datas = (this.isBoldStyle() ? bigFontBoldDatas : bigFontDatas);

      if (datas[ch] == null) {
        List<Font> fonts = this.deriveFonts(12 * scaleFactor * 4, this.isBoldStyle() ? Font.BOLD : Font.PLAIN);

        if (this.isBoldStyle()) {
          bigFontBoldDatas[ch] = new CharacterTextureData(fonts, ch, scaleFactor * 4);
        } else {
          bigFontDatas[ch] = new CharacterTextureData(fonts, ch, scaleFactor * 4);
        }
      }
      return (this.isBoldStyle() ? bigFontBoldDatas[ch] : bigFontDatas[ch]);
    } else if (this.isBoldStyle()) {
      if (boldDatas[p] == null) {
        List<Font> fonts = this.deriveFonts(12 * scaleFactor, Font.BOLD);
        boldDatas[p] = new FontTextureData(fonts, p, scaleFactor);
      }
      return boldDatas[p];
    } else {
      if (fontDatas[p] == null) {
        List<Font> fonts = this.deriveFonts(12 * scaleFactor);
        fontDatas[p] = new FontTextureData(fonts, p, scaleFactor);
      }
      return fontDatas[p];
    }
  }

  private boolean isBoldStyle() {
    if (this.lastRenderCharAtPosEvent == null) {
      return false;
    }

    return this.lastRenderCharAtPosEvent.boldStyle;
  }

}
