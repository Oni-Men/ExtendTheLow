package onim.en.etl.ui.components;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.ui.RenderingContext;

public class ListView extends ComponentBase {

  private float scroll = 0;
  private float initialMouseClickY = -1F;

  private List<String> list;
  private Consumer<String> onclick;

  private int index = 0;

  public ListView(int width, int height) {
    this.width = width;
    this.height = height;

    this.list = new ArrayList<>();
  }

  @Override
  public int draw(Minecraft mc) {
    if (list.isEmpty()) {
      return height;
    }

    boolean isHovering = RenderingContext.isHovering(this);

    RenderingContext.push();
    RenderingContext ctx = RenderingContext.current;

    int actualHeight = Math.min(height, list.size() * 10);
    index = (int) Math.floor(ctx.mouseY - ctx.y - scroll) / 10;

    if (isHovering) {
      if (!Mouse.isButtonDown(0)) {
        while (Mouse.next()) {
          int delta = Mouse.getEventDWheel();
          if (delta != 0) {
            this.scroll += delta / 8F;
          }
        }
        initialMouseClickY = -1F;
      }
    }

    if (scroll > 0) {
      scroll = 0;
    }
    if (scroll < -(list.size() * 10 - actualHeight)) {
      scroll = -(list.size() * 10 - actualHeight);
    }

    ScaledResolution res = new ScaledResolution(mc);
    int scaleW = (int) (mc.displayWidth / res.getScaledWidth_double());
    int scaleH = (int) (mc.displayHeight / res.getScaledHeight_double());
    GL11.glEnable(GL11.GL_SCISSOR_TEST);
    GL11.glScissor(ctx.x * scaleW, ctx.y * scaleH, width * scaleW, height * scaleH);

    GlStateManager.pushMatrix();
    GlStateManager.translate(ctx.x, scroll + ctx.y, 0);

    int renderFrom = (int) Math.max(0, Math.abs(scroll) / 10 - 1);
    int renderTo = (int) Math.min(list.size(), (Math.abs(scroll) + height) / 10 + 1);

    for (int i = renderFrom; i < renderTo; i++) {
      String s = ExtendTheLow.AdvancedFont.trimStringToWidth(list.get(i), width);
      if (isHovering && i == index) {
        s = EnumChatFormatting.UNDERLINE + s;
      }
      this.drawString(ExtendTheLow.AdvancedFont, s, 0, i * 10, 0xFFFFFF);
    }

    RenderingContext.pop();

    GL11.glDisable(GL11.GL_SCISSOR_TEST);
    GlStateManager.popMatrix();

    return height;
  }

  @Override
  public void mousePressed(int button) {
    if (button != 0) {
      return;
    }
    if (initialMouseClickY == -1F) {
      initialMouseClickY = RenderingContext.current.mouseY;
    }
  }

  @Override
  public void mouseReleased(int button, boolean isInside) {
    if (initialMouseClickY == -1F) {
      initialMouseClickY = RenderingContext.current.mouseY;
      if (index >= 0 && index < list.size()) {
        if (this.onclick != null) {
          this.onclick.accept(list.get(index));
        }
      }
    }
  }

  @Override
  public void mouseDragged() {
    if (initialMouseClickY >= 0F) {
      scroll += RenderingContext.current.mouseY - initialMouseClickY;
      initialMouseClickY = RenderingContext.current.mouseY;
    }
  }

  public void setList(List<String> list) {
    this.list = list;
  }

  public List<String> getList() {
    return this.list;
  }

  public int size() {
    return this.list.size();
  }

  public String get(int i) {
    if (i < 0 || i >= list.size()) {
      return null;
    }
    return list.get(i);
  }

  public String remove(int i) {
    if (i < 0 || i >= list.size()) {
      return null;
    }
    return list.remove(i);
  }

  public boolean remove(String s) {
    return this.list.remove(s);
  }

  public void add(String s) {
    this.list.add(s);
  }

  public void add(int i, String s) {
    this.list.add(i, s);
  }

  public void setOnClick(Consumer<String> onclick) {
    this.onclick = onclick;
  }

}
