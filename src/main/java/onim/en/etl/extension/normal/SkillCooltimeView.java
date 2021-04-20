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
import onim.en.etl.util.ColorUtil;
import onim.en.etl.util.Easing;
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
  public void onEnable() {}

  @Override
  public void onDisable() {}

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

    float elapsed = data.getElapsedSeconds();
    float remaining = data.getRemainingSeconds();

    if (remaining < -3F) {
      return false;
    }

    String text = TheLowUtil.formatCooltime(remaining) + " " + data.getSkillName();

    int i = font.getStringWidth(text);
    int left = x - i - 16;

    float alpha = Math.abs(MathHelper.sin(remaining * (float) Math.PI));

    if (elapsed < 0.5F) {
      alpha = (elapsed / 0.5F);
      left += (1 - Easing.easeOutCubic(alpha)) * i;
    }

    if (remaining < -2.5F) {
      alpha = (3F - Math.abs(remaining)) / 0.5F;
      left += (1 - Easing.easeInCubic(alpha)) * i;
    }

    int color = ColorUtil.applyAlpha(0xCC3333, alpha);
    if (remaining < 0) {
      color = 0x5533CC33;
    }

    GuiUtil.drawGradientRectHorizontal(left - 8, y - 10, left + i / 3, y, 0, color);
    GuiUtil.drawGradientRectHorizontal(left + i / 3, y - 10, left + i + 16, y, color, color);

    color = color | 0xFFFFFF;
    if (elapsed < 0.5F || remaining > -2.5F) {
      color = 0xFFFFFFFF;
    }

    if (ColorUtil.getAlpha(color) > 0.08F) {
      font.drawString(text, left + 8, y - 10, color);
    }

    return true;
  }
}
