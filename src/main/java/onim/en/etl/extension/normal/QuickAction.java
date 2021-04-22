package onim.en.etl.extension.normal;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.action.QuickActionExecutor;
import onim.en.etl.extension.TheLowExtension;
import onim.en.etl.ui.RenderingContext;
import onim.en.etl.util.ColorUtil;
import onim.en.etl.util.Easing;
import onim.en.etl.util.GuiUtil;

public class QuickAction extends TheLowExtension {

  private boolean displayed = false;

  private String actionId = null;

  private int frames = 0;

  @Override
  public String id() {
    return "onim.en.etl.quickAction";
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
  public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
    Minecraft mc = Minecraft.getMinecraft();
    if (event.type != ElementType.CHAT) {
      return;
    }

    if (mc.currentScreen == null && !mc.inGameHasFocus) {
      if (ExtendTheLow.keyQuickAction.isKeyDown()) {
        this.frames++;
        this.drawQuickActionMenu(event.resolution);
        displayed = true;
        return;
      } else if (displayed) {
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        mc.setIngameFocus();
        displayed = false;

        QuickActionExecutor.execute(actionId);
      }
    }
    this.frames = 0;
  }

  private void drawQuickActionMenu(ScaledResolution resolution) {
    float ratio = Easing.easeOutCubic(MathHelper.clamp_float(frames / 10F, 0, 1));

    Minecraft mc = Minecraft.getMinecraft();
    int width = resolution.getScaledWidth();
    int height = resolution.getScaledHeight();

    int mouseX = Mouse.getEventX() * width / mc.displayWidth - width / 2;
    int mouseY = Mouse.getEventY() * height / mc.displayHeight - height / 2;

    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.translate(width / 2F, height / 2F, 0);

    String[] actionIds = QuickActionExecutor.getActionIds();

    float tileAngle = 360F / actionIds.length;
    for (int i = 0; i < actionIds.length; i++) {
      float rad1 = (float) Math.toRadians((i) * tileAngle);
      float rad2 = (float) Math.toRadians((i + 1) * tileAngle);
      double degree = Math.toDegrees(Math.atan2(-mouseY, mouseX));
      if (degree < 0) {
        degree += 360;
      }

      degree = degree % 360;

      ColorUtil.glColor(0x88336699);
      if (Math.hypot(mouseX, mouseY) > 24) {
        if ((int) (degree / tileAngle) == i) {
          ColorUtil.glColor(0x886699BB);
          this.actionId = actionIds[i];
        }
      } else {
        this.actionId = null;
      }

      GlStateManager.disableTexture2D();
      GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

      this.addVertex(rad1, 96 * ratio);
      this.addVertex(rad1, 24);
      this.addVertex(rad2, 96 * ratio);
      this.addVertex(rad2, 24);
      GL11.glEnd();

      GlStateManager.enableTexture2D();

      Vector2f vec1 = new Vector2f(MathHelper.cos(rad1), MathHelper.sin(rad1));
      Vector2f vec2 = new Vector2f(MathHelper.cos(rad2), MathHelper.sin(rad2));

      vec1.scale(60);
      vec2.scale(60);

      GlStateManager.pushMatrix();
      GlStateManager.translate((vec2.x + vec1.x) / 2, (vec2.y + vec1.y) / 2, 1);

      String[] lines = I18n.format(actionIds[i]).split("\n");

      if (ratio > 0.08F) {
        RenderingContext.push();
        RenderingContext.color(ColorUtil.applyAlpha(0xFFFFFFFF, ratio));

        for (int k = 0; k < lines.length; k++) {
          GuiUtil.drawCenteredString(lines[k].trim(), 0, k * 10 - lines.length / 2F * 10, true);
        }

        RenderingContext.pop();
      }

      GlStateManager.popMatrix();
    }
    GlStateManager.popMatrix();

    if (ratio > 0.08F) {
      String s = this.actionId != null ? this.actionId : I18n.format(this.id());
      RenderingContext.color(ColorUtil.applyAlpha(0xFFFFFFFF, ratio));
      GuiUtil.drawCenteredString(I18n.format(s), width / 2, height / 2 - 104, true);
    }
  }

  private void addVertex(float rad, float radius) {
    float x = radius * MathHelper.cos(rad);
    float y = radius * MathHelper.sin(rad);
    GL11.glVertex3f(x, y, 0F);
  }
}
