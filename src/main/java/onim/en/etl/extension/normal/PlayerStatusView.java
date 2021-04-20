package onim.en.etl.extension.normal;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
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
  public void onRenderPlayer(RenderPlayerEvent.Post event) {
    if (!TheLowUtil.isPlayingTheLow()) {
      return;
    }

    RenderManager renderManager = event.renderer.getRenderManager();
    this.renderStatusView(renderManager, event.entityPlayer, event.partialRenderTick);
  }

  private void renderStatusView(RenderManager renderManager, EntityPlayer target, float partialTick) {
    PlayerStatus status = DataStorage.getStatusByUniqueId(target.getUniqueID());

    if (status == null) {
      return;
    }

    double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * partialTick;
    double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * partialTick;
    double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * partialTick;

    x -= renderManager.viewerPosX;
    y -= renderManager.viewerPosY;
    z -= renderManager.viewerPosZ;

    Vector3f vec3f = new Vector3f((float) x, (float) y, (float) z);
    float a = this.alphaByAngle(vec3f, renderManager);

    boolean detailed = a > 0.75;

    GlStateManager.disableLighting();
    GlStateManager.enableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y + target.height, z);
    GL11.glNormal3f(0F, 1F, 0F);
    GlStateManager.rotate(-renderManager.playerViewY, 0F, 1F, 0F);
    GlStateManager.rotate(renderManager.playerViewX, 1F, 0F, 0F);
    GlStateManager.scale(-0.0266667F, -0.0266667F, 0.0266667F);


    int i = -40;
    if (detailed || showReincCount) {
      String text = I18n.format("onim.en.etl.playerStatusView.reincarnationCount", status.getReinCount());
      this.drawText(text, 0, i, a);
      i -= 10;

    }

    if (detailed || showMainLevel) {
      String text = I18n.format("onim.en.etl.playerStatusView.mainLevel", status.mainLevel);
      this.drawText(text, 0, i, a);
      i -= 10;
    }

    if ((detailed || showClanName) && status.clanInfo != null) {
      String text = I18n.format("onim.en.etl.playerStatusView.clanInfo", status.clanInfo.clanName);
      this.drawText(text, 0, i, a);
    }
    GlStateManager.popMatrix();

    GlStateManager.enableDepth();
    GlStateManager.depthMask(true);
    GlStateManager.enableAlpha();
    GlStateManager.enableLighting();
  }

  private void drawText(String text, int x, int y, float a) {
    int i = ExtendTheLow.AdvancedFont.getStringWidth(text);

    if (a < 0.08) {
      return;
    }

    GlStateManager.depthMask(false);
    GlStateManager.disableDepth();
    GuiUtil.drawGradientRectHorizontal(x - i / 2 - 2, y, x + i / 2 + 2, y + 8, 0x66668866, 0x66333366);

    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

    ExtendTheLow.AdvancedFont.drawString(text, x - i / 2, y, ColorUtil.applyAlpha(0x00FFFFFF, a * 0.2F));
    GlStateManager.enableDepth();
    GlStateManager.depthMask(true);
    ExtendTheLow.AdvancedFont.drawString(text, x - i / 2, y, ColorUtil.applyAlpha(0x00FFFFFF, a));
  }

  private float alphaByAngle(Vector3f loc, RenderManager renderManager) {
    float look = (float) Math.toRadians(renderManager.playerViewY);

    Vector2f locationVec = new Vector2f(loc.z, -loc.x);
    Vector2f viewVec = new Vector2f(MathHelper.cos(look), MathHelper.sin(look));

    float angle = Vector2f.angle(locationVec, viewVec) / (float) Math.PI;

    return MathHelper.clamp_float(1F - angle * 4F, 0F, 1F);
  }

}
