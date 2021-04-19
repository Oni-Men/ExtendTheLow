package onim.en.etl.extension.normal;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.api.DataStorage;
import onim.en.etl.api.dto.PlayerStatus;
import onim.en.etl.extension.TheLowExtension;
import onim.en.etl.ui.AdvancedFontRenderer;
import onim.en.etl.util.ColorUtil;
import onim.en.etl.util.TheLowUtil;

public class PlayerStatusView extends TheLowExtension {

  private int countShowDetail = 0;

  @Override
  public String id() {
    return "onim.en.etl.playerStatusView";
  }

  @Override
  public String category() {
    return "onim.en.etl.category.rendering";
  }

  @Override
  public void onEnable() {
    // TODO Auto-generated method stub
  }

  @Override
  public void onDisable() {
    // TODO Auto-generated method stub
  }

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

    boolean detailed = renderManager.pointedEntity != null && renderManager.pointedEntity.equals(target);

    if (detailed) {
      this.countShowDetail++;
    } else {
      this.countShowDetail = 0;
    }

    double x = target.lastTickPosX + (target.posX - target.lastTickPosX) * partialTick;
    double y = target.lastTickPosY + (target.posY - target.lastTickPosY) * partialTick;
    double z = target.lastTickPosZ + (target.posZ - target.lastTickPosZ) * partialTick;

    Vector3f vec3f = new Vector3f((float) x, (float) y, (float) z);
    float a = this.alphaByAngle(vec3f, renderManager);
    
    x -= renderManager.viewerPosX;
    y -= renderManager.viewerPosY;
    z -= renderManager.viewerPosZ;

    GlStateManager.disableLighting();
    GlStateManager.depthMask(false);
    GlStateManager.disableDepth();
    GlStateManager.enableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.color(1F, 1F, 1F);

    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y + target.height, z);
    GL11.glNormal3f(0F, 1F, 0F);
    GlStateManager.rotate(-renderManager.playerViewY, 0F, 1F, 0F);
    GlStateManager.rotate(renderManager.playerViewX, 1F, 0F, 0F);
    GlStateManager.scale(-0.0266667F, -0.0266667F, 0.0266667F);
    AdvancedFontRenderer font = ExtendTheLow.AdvancedFont;
    String text = String.format("Rein. %d times", status.getReinCount());

    int i = font.getStringWidth(text);
    font.drawString(text, -i / 2, -40, ColorUtil.applyAlpha(553648127, a));
    GlStateManager.enableDepth();
    GlStateManager.depthMask(true);
    font.drawString(text, -i / 2, -40, ColorUtil.applyAlpha(-1, a));

    GlStateManager.popMatrix();

    GlStateManager.enableDepth();
    GlStateManager.depthMask(true);
    GlStateManager.enableAlpha();
    GlStateManager.enableLighting();
  }

  private float alphaByAngle(Vector3f loc, RenderManager renderManager) {
    float x = (float) (loc.x - renderManager.viewerPosX);
    float z = (float) (loc.y - renderManager.viewerPosZ);

    float look = (float) Math.toRadians(renderManager.playerViewY);

    float cos = MathHelper.cos(look);
    float sin = MathHelper.sin(look);

    Vector2f locationVec = new Vector2f(z, -x);
    Vector2f viewVec = new Vector2f(cos, sin);

    float angle = (float) Math.toDegrees(Vector2f.angle(locationVec, viewVec));

    return MathHelper.clamp_float(1F - (angle / 15F), 0F, 1F);
  }
}
