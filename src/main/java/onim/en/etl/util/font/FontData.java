package onim.en.etl.util.font;

import org.lwjgl.util.vector.Vector2f;

import net.minecraft.client.renderer.texture.TextureUtil;

public abstract class FontData {

  protected boolean isInitialized = false;
  protected int glTextureId = -1;
  protected float[] glyphWidth;
  protected float[] baselines;
  protected int width;
  protected int height;
  protected int scale;
  protected Vector2f[] uvCoords;

  public void destroy() {
    TextureUtil.deleteTexture(this.glTextureId);
  }

  public void setGlTextureId(int glTextureId) {
    this.glTextureId = glTextureId;
  }

  public int getGlTextureId() {
    return this.glTextureId;
  }

  public int getScale() {
    return this.scale;
  }

  public float getCharWidth(char ch) {
    return this.glyphWidth[ch % 256];
  }

  public Vector2f getUVCoord(char ch) {
    return this.uvCoords[ch % 256];
  }

  public float getCharHeight() {
    return (float) this.height / (16 * scale);
  }

  public float getTextureWidth() {
    return (float) this.width;
  }

  public float getTextureHeight() {
    return (float) this.height;
  }

  public boolean isInitialized() {
    return this.isInitialized;
  }

  public void complete() {
    this.isInitialized = true;
  }

  public float getBaseline(char ch) {
    return this.baselines[ch % 256];
  }
}
