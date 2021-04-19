package onim.en.etl.ui.parts;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import onim.en.etl.ExtendTheLow;

public class ListView extends Gui {

  private float scroll = 0;
  private float initialMouseClickY = -1F;

  public int left;
  public int top;
  public int width;
  public int height;

  private List<String> list;
  private Consumer<String> onclick;

  private long frames = 0;

  public ListView(int x, int y, int width, int height) {
    this.left = x;
    this.top = y;
    this.width = width;
    this.height = height;

    this.list = new ArrayList<>();
  }

  public void drawListView(int mouseX, int mouseY) {
    frames++;
    if (list.isEmpty()) {
      return;
    }

    Minecraft mc = Minecraft.getMinecraft();
    boolean isHovering = mouseX >= this.left && mouseX <= this.left + this.width && mouseY >= this.top
        && mouseY <= this.top + this.height;

    int actualHeight = Math.min(height, list.size() * 10);
    int index = (int) Math.floor(mouseY - top - scroll) / 10;

    if (isHovering && frames > 6) {
      if (Mouse.isButtonDown(0)) {
        if (initialMouseClickY == -1F) {

          if (index >= 0 && index < list.size()) {
            if (this.onclick != null) {
              this.onclick.accept(list.get(index));
            }
          }

          initialMouseClickY = mouseY;
        } else if (initialMouseClickY >= 0F) {
          scroll += mouseY - initialMouseClickY;
          initialMouseClickY = mouseY;
        }
      } else {
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
    GL11.glScissor(left * scaleW, top * scaleW, width * scaleW, height * scaleH);

    GlStateManager.pushMatrix();
    GlStateManager.translate(0, scroll, 0);

    int renderFrom = (int) Math.max(0, Math.abs(scroll) / 10 - 1);
    int renderTo = (int) Math.min(list.size(), (Math.abs(scroll) + height) / 10 + 1);

    for (int i = renderFrom; i < renderTo; i++) {
      String s = ExtendTheLow.AdvancedFont.trimStringToWidth(list.get(i), width);
      if (isHovering && i == index) {
        s = EnumChatFormatting.UNDERLINE + s;
      }
      this.drawString(ExtendTheLow.AdvancedFont, s, left, top + i * 10, 0xFFFFFF);
    }

    GL11.glDisable(GL11.GL_SCISSOR_TEST);
    GlStateManager.popMatrix();

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
