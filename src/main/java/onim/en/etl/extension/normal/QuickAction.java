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
import onim.en.etl.util.GuiUtil;

public class QuickAction extends TheLowExtension {

  private boolean displayed = false;

  private String actionId = null;

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
    if (event.type != ElementType.CHAT) {
      return;
    }

    Minecraft mc = Minecraft.getMinecraft();
    if (mc.currentScreen != null) {
      return;
    }

    if (mc.inGameHasFocus) {
      return;
    }

    if (ExtendTheLow.keyQuickAction.isKeyDown()) {
      this.drawQuickActionMenu(event.resolution);
      displayed = true;
    } else if (displayed) {
      Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
      mc.setIngameFocus();
      displayed = false;

      QuickActionExecutor.execute(actionId);
    }

  }

  private void drawQuickActionMenu(ScaledResolution resolution) {
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

      GlStateManager.color(0F, 0F, 0F, 0.3F);
      if (Math.hypot(mouseX, mouseY) > 24) {
        if ((int) (degree / tileAngle) == i) {
          GlStateManager.color(1F, 1F, 1F, 0.3F);
          this.actionId = actionIds[i];
        }
      } else {
        this.actionId = null;
      }

      GlStateManager.disableTexture2D();
      GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

      this.addVertex(rad1, 96);
      this.addVertex(rad1, 24);
      this.addVertex(rad2, 96);
      this.addVertex(rad2, 24);
      GL11.glEnd();

      GlStateManager.enableTexture2D();

      Vector2f vec1 = new Vector2f(MathHelper.cos(rad1), MathHelper.sin(rad1));
      Vector2f vec2 = new Vector2f(MathHelper.cos(rad2), MathHelper.sin(rad2));

      vec1.scale(60);
      vec2.scale(60);

      GlStateManager.pushMatrix();
      GlStateManager.translate((vec2.x + vec1.x) / 2, (vec2.y + vec1.y) / 2, 1);
      String s = I18n.format(actionIds[i]);
      GlStateManager.scale(0.5, 0.5, 1);
      GuiUtil.drawCenteredString(s, 0, 0, true);
      GlStateManager.popMatrix();
    }
    GlStateManager.popMatrix();

    String s = this.actionId != null ? this.actionId : I18n.format(this.id());
    GuiUtil.drawCenteredString(I18n.format(s), width / 2, height / 2 - 104, true);
  }

  private void addVertex(float rad, float radius) {
    float x = radius * MathHelper.cos(rad);
    float y = radius * MathHelper.sin(rad);
    GL11.glVertex3f(x, y, 0F);
  }
}
