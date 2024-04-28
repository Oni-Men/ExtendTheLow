package onim.en.etl.extension.normal;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.annotation.PrefItem;
import onim.en.etl.api.DataStorage;
import onim.en.etl.api.dto.PlayerStatus;
import onim.en.etl.extension.TheLowExtension;
import onim.en.etl.util.ColorUtil;
import onim.en.etl.util.GuiUtil;
import onim.en.etl.util.TheLowUtil;

public class PlayerStatusView extends TheLowExtension {

  @PrefItem(id = "onim.en.etl.playerStatusView.showReincCount", type = boolean.class)
  public boolean showReincCount = true;

  @PrefItem(id = "onim.en.etl.playerStatusView.showMainLevel", type = boolean.class)
  public boolean showMainLevel = false;

  @PrefItem(id = "onim.en.etl.playerStatusView.showClanName", type = boolean.class)
  public boolean showClanName = false;
  
  @PrefItem(id = "onim.en.etl.playerStatusView.showJobName", type = boolean.class)
  public boolean showJobName = false;

  @PrefItem(id = "onim.en.etl.playerStatusView.scale", type = float.class, min = 0.1F, max = 1.5F, step = 0.05F,
      format = "x%.2f")
  public float scale = 1.0F;

  @Override
  public String id() {
    return "onim.en.etl.playerStatusView";
  }

  @Override
  public String category() {
    return "onim.en.etl.category.rendering";
  }

  @Override
  public void onEnable() {}

  @Override
  public void onDisable() {}

  @SubscribeEvent
  public void onRenderPlayer(RenderLivingEvent.Specials.Pre<AbstractClientPlayer> event) {
    if (!TheLowUtil.isPlayingTheLow()) {
      return;
    }

    EntityLivingBase entity = event.entity;

    if (event.entity instanceof EntityPlayer) {
      PlayerStatus status = DataStorage.getStatusByUniqueId(entity.getUniqueID());
      if (status == null) {
        return;
      }

      event.setCanceled(true);
      RenderManager renderManager = event.renderer.getRenderManager();

      double distanceSq = entity.getDistanceSqToEntity(renderManager.livingPlayer);
      double range = entity.isSneaking() ? RendererLivingEntity.NAME_TAG_RANGE_SNEAK
          : RendererLivingEntity.NAME_TAG_RANGE;

      if (distanceSq > range * range) {
        return;
      }

      Vector3f vec3f = new Vector3f((float) event.x, (float) event.y, (float) event.z);
      float a = Math.max(0.2F, this.alphaByAngle(vec3f, renderManager));
      if (entity.isSneaking()) {
        a *= 0.75F;
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate(event.x, event.y + entity.height, event.z);

      GL11.glNormal3f(0F, 1F, 0F);
      GlStateManager.rotate(-renderManager.playerViewY, 0F, 1F, 0F);
      Minecraft mc = Minecraft.getMinecraft();
      if (mc.gameSettings.thirdPersonView == 2) {
        GlStateManager.rotate(-renderManager.playerViewX, 1F, 0F, 0F);
      } else {
        GlStateManager.rotate(renderManager.playerViewX, 1F, 0F, 0F);
      }

      GlStateManager.scale(-0.0266667F, -0.0266667F, 0.0266667F);

      if (!entity.isSneaking()) {
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        this.renderStatusView(renderManager, (EntityPlayer) entity, status, 0.4F);
      }
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      this.renderStatusView(renderManager, (EntityPlayer) entity, status, a);

      GlStateManager.popMatrix();
    }
  }

  private void renderStatusView(RenderManager renderManager, EntityPlayer target, PlayerStatus status, float a) {
    boolean detailed = a > 0.75;

    GlStateManager.enableBlend();
    GlStateManager.disableLighting();
    GlStateManager.disableTexture2D();
    GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);

    GlStateManager.pushMatrix();
    GlStateManager.translate(0, -30, 0);
    GlStateManager.scale(scale, scale, scale);

    // HP BAR Start
    float healthRatio = MathHelper.clamp_float(target.getHealth() / target.getMaxHealth(), 0F, 1F);
    int bgColor = ColorUtil.applyAlpha(0xFF303030, a);

    int barWidth = 40;
    int barWidthRatio = (int) (barWidth * healthRatio);

    GuiUtil.drawGradientRectHorizontal(barWidth
        - (int) (2 * (barWidth - barWidthRatio)), -2, barWidth, 6, bgColor, bgColor);
    GuiUtil
      .drawGradientRectHorizontal(-barWidth, -2, (int) (2 * barWidthRatio) - barWidth, 6, ColorUtil
        .applyAlpha(applyColorHealthBar(0xFF336633, healthRatio), a), ColorUtil
          .applyAlpha(applyColorHealthBar(0xFF336699, healthRatio), a));
    GL11.glPushAttrib(GL11.GL_DEPTH_BUFFER_BIT);
    GlStateManager.pushMatrix();
    GlStateManager.translate(0, 0, -0.01);
    GlStateManager.scale(0.75, 0.75, 0.75);

    // GlStateManager.disableDepth();
    String hpText = String.format("%d/%d", Math.round(target.getHealth()), Math.round(target.getMaxHealth()));
    this.drawText(hpText, 0, -1, a, true);
    GL11.glPopAttrib();
    GlStateManager.popMatrix();
    // HP BAR End

    // MCID and ClanName
    GlStateManager.pushMatrix();
    GlStateManager.translate(-barWidth, -10, 0);
    GlStateManager.scale(0.75, 0.75, 0.75);
    int i = this.drawText(status.mcid, 0, 0, a, false);
    GlStateManager.translate(i + 1, 0, 0);
    GlStateManager.scale(0.75, 0.75, 0.75);
    int j = this.drawText(TheLowUtil.formatClanName(status.clanInfo), 0, 2, a, false);
    if (this.showJobName) {
      GlStateManager.translate(j + 1, 0, 0);
      this.drawText(TheLowUtil.formatJobName("§b" + status.jobName), 0,  2, a, false);
    }
    GlStateManager.popMatrix();

    // Reinc count and main level
    GlStateManager.pushMatrix();
    GlStateManager.translate(-barWidth, 8, 0);
    GlStateManager.scale(0.75, 0.75, 0.75);

    List<String> details = new ArrayList<>();
    if (detailed || showReincCount) {
      details.add(I18n.format("onim.en.etl.playerStatusView.reincarnationCount", status.getReinCount()));
    }

    if (detailed || showMainLevel) {
      details.add(I18n.format("onim.en.etl.playerStatusView.mainLevel", status.mainLevel));
    }
    this.drawText(String.join(" / ", details), 0, 0, a, false);

    GlStateManager.popMatrix();
    GlStateManager.popMatrix();

    GlStateManager.enableDepth();
    GlStateManager.depthMask(true);
    GlStateManager.enableAlpha();
    GlStateManager.enableLighting();
  }

  private int drawText(String text, int x, int y, float a, boolean centered) {
    int i = ExtendTheLow.AdvancedFont.getStringWidth(text);
    if (a < 0.08) {
      return 0;
    }

    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    ExtendTheLow.AdvancedFont.drawString(text, x + (centered ? -i / 2 : 0), y, ColorUtil.applyAlpha(0x00FFFFFF, a));

    return i;
  }

  private int applyColorHealthBar(int color, float ratio) {
    float red = ColorUtil.getRed(color);
    float green = ColorUtil.getGreen(color);
    float blue = ColorUtil.getBlue(color);

    red = MathHelper.clamp_float(red + (0.75F - ratio), 0F, 1F);
    green = green * (ratio - (green / 255F));
    blue = blue * (ratio - (blue / 255F));

    color = ColorUtil.applyBlue(color, blue);
    color = ColorUtil.applyGreen(color, green);
    color = ColorUtil.applyRed(color, red);

    return ColorUtil.scale(color, 1.2F);
  }

  private float alphaByAngle(Vector3f loc, RenderManager renderManager) {
    float look = (float) Math.toRadians(renderManager.playerViewY);

    Vector2f locationVec = new Vector2f(loc.z, -loc.x);
    Vector2f viewVec = new Vector2f(MathHelper.cos(look), MathHelper.sin(look));

    float angle = Vector2f.angle(locationVec, viewVec) / (float) Math.PI;

    return MathHelper.clamp_float(1F - angle * 4F, 0F, 1F);
  }

}
