package onim.en.etl.extension.normal;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import onim.en.etl.event.SkillEnterCooltimeEvent;
import onim.en.etl.extension.TheLowExtension;
import onim.en.etl.util.GuiUtil;
import onim.en.etl.util.TheLowUtil;

public class SkillCooltimeView extends TheLowExtension {

  private HashMap<String, SkillEnterCooltimeEvent> cooltimeList = Maps.newHashMap();

  @Override
  public String id() {
    return "onim.en.etl.skillCooltimeView";
  }

  @Override
  public String category() {
    return "onim.en.etl.category.util";
  }

  @Override
  public void onEnable() {

  }

  @Override
  public void onDisable() {

  }

  @SubscribeEvent
  public void onSkillEnterCooltime(SkillEnterCooltimeEvent event) {
    cooltimeList.put(event.getSkillName(), event);
  }

  @SubscribeEvent
  public void onRenderGameOverlay(RenderGameOverlayEvent event) {
    if (event.type != ElementType.HOTBAR)
      return;

    int i = 1;
    int height = event.resolution.getScaledHeight();
    int width = event.resolution.getScaledWidth();
    Iterator<Entry<String, SkillEnterCooltimeEvent>> itr = cooltimeList.entrySet().iterator();
    Entry<String, SkillEnterCooltimeEvent> next;
    while (itr.hasNext()) {
      next = itr.next();
      boolean rendered = this.renderCooltimeView(next.getValue(), width, height - i * 12 - 32);
      if (!rendered) {
        itr.remove();
        continue;
      }
      i++;
    }
  }

  private boolean renderCooltimeView(SkillEnterCooltimeEvent data, int x, int y) {
    Minecraft mc = Minecraft.getMinecraft();
    FontRenderer font = mc.fontRendererObj;

    long t = data.getRemainingTicks();

    if (t < -3000) {
      return false;
    }

    String text = TheLowUtil.formatCooltime(t) + " " + data.getSkillName();

    int i = font.getStringWidth(text);
    int left = x - i - 16;

    float alpha = Math.abs(MathHelper.sin(t / 1000F * (float) Math.PI));
    int color = 0xCC3333 | ((int) (alpha * 255) << 24);

    if (t < 0) {
      color = 0x5533CC33;
    }

    long elapsed = System.currentTimeMillis() - data.getCooltimeStartsWhen();
    if (elapsed < 500) {
      alpha = (elapsed / 500F);
      color = color & 0x00FFFFFF;
      color = color | ((int) (alpha * 0x55)) << 24;
    }

    if (t < 0 && t > -500) {
      alpha = (float) -Math.pow((t + 250) / 500F, 2) + 1F;
      color = color & 0x00FFFFFF;
      color = color | ((int) (alpha * 0xDD) & 0xFF) << 24;
    }

    if (t < -2500) {
      alpha = (3000 - Math.abs(t)) / 500F;
      color = color & 0x00FFFFFF;
      color = color | ((int) (alpha * 0x33) & 0xFF) << 24;
    }

    GuiUtil.drawGradientRectHorizontal(left - 8, y - 10, left + i / 3, y, 0, color);
    GuiUtil.drawGradientRectHorizontal(left + i / 3, y - 10, left + i + 16, y, color, color);

    color = color | 0xFFFFFF;
    if (elapsed < 500 || t > -2500) {
      color = 0xFFFFFFFF;
    }

    if (((color >> 24) & 255) > 4) {
      font.drawString(text, left + 8, y - 10, color);
    }

    return true;
  }
}
