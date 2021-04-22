package onim.en.etl.extension.normal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.annotation.PrefItem;
import onim.en.etl.api.DataStorage;
import onim.en.etl.api.HandleAPI;
import onim.en.etl.api.dto.DungeonInfo;
import onim.en.etl.extension.TheLowExtension;
import onim.en.etl.ui.AdvancedFontRenderer;
import onim.en.etl.util.ColorUtil;
import onim.en.etl.util.TheLowUtil;

public class DungeonMarker extends TheLowExtension {

  private static final ResourceLocation MARKER_BG =
      new ResourceLocation("onim.en.etl:textures/marker_bg.png");

  private boolean worldHasLoaded = false;

  @PrefItem(id = "onim.en.etl.dungeonMarker.distanceAlwaysRender", type = float.class, min = 0F,
      max = 500F, unit = "m", step = 10F)
  public float distanceAlwaysRender = 50F;

  @PrefItem(id = "onim.en.etl.dungeonMarker.maxRenderDistance", type = float.class, min = 100F,
      max = 3000F, unit = "m", step = 100F)
  public float maxRenderDistance = 200F;

  @PrefItem(id = "onim.en.etl.dungeonMarker.levelLowest", type = int.class, min = 0F, max = 80F,
      unit = "lv", step = 10F)
  public int displayLevelLowest = 0;

  @PrefItem(id = "onim.en.etl.dungeonMarker.levelHighest", type = int.class, min = 0F, max = 80F,
      unit = "lv", step = 10F)
  public int displayLevelHighest = 0;

  @PrefItem(id = "onim.en.etl.dungeonMarker.displaySpecials", type = boolean.class)
  public boolean displaySpecials = false;

  @PrefItem(id = "onim.en.etl.dungeonMarker.scale", type = float.class, min = 0.05F, max = 2.0F, step = 0.05F,
      format = "x%.2f")
  public float configScale;

  @Override
  public String id() {
    return "onim.en.etl.dungeonMarker";
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
  public void onInitGui(GuiScreenEvent.InitGuiEvent.Post event) {
    if (event.gui instanceof GuiDisconnected) {
      DataStorage.clearDungeons();
    }
  }

  @SubscribeEvent
  public void onOpenGui(GuiOpenEvent event) {
    if (TheLowUtil.isPlayingTheLow() && event.gui == null && worldHasLoaded) {
      HandleAPI.sendRequest("location");
      worldHasLoaded = false;
    }
  }

  @SubscribeEvent
  public void onWorldLoad(WorldEvent.Load event) {
    worldHasLoaded = true;
  }

  @SubscribeEvent
  public void onRenderWorldLast(RenderWorldLastEvent event) {
    Minecraft mc = Minecraft.getMinecraft();
    RenderManager renderManager = mc.getRenderManager();

    if (!TheLowUtil.isPlayingTheLow())
      return;

    if (!DataStorage.getCurrentWorldName().equals("thelow")) {
      return;
    }

    if (renderManager == null)
      return;

    GlStateManager.disableDepth();
    GlStateManager.enableBlend();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.pushMatrix();

    double maxDistance = mc.gameSettings.renderDistanceChunks * 16D * 0.99D;

    List<DungeonInfo> dungeons = new ArrayList<>(DataStorage.getDungeons());
    Collections.sort(dungeons, (a, b) -> {
      Vector3f locA = a.getLocation();
      Vector3f locB = b.getLocation();
      double distA = renderManager.getDistanceToCamera(locA.x, locA.y, locA.z);
      double distB = renderManager.getDistanceToCamera(locB.x, locB.y, locB.z);
      return (int) (Math.round(distB) - Math.round(distA));
    });

    for (DungeonInfo dungeonInfo : dungeons) {
      int level = dungeonInfo.getLevel();
      if (level == 9999 && !this.displaySpecials) {
        continue;
      }
      if (level != 9999 && (level < displayLevelLowest || level > displayLevelHighest)) {
        continue;
      }
      this.renderDungeonMarker(renderManager, dungeonInfo, maxDistance);
    }

    GlStateManager.popMatrix();
    GlStateManager.enableDepth();
    GlStateManager.disableBlend();
    GlStateManager.color(1F, 1F, 1F, 1F);
  }

  private void renderDungeonMarker(RenderManager renderManager, DungeonInfo dungeonInfo,
      double maxDistance) {
    Vector3f loc = dungeonInfo.getLocation();

    double distance = Math.sqrt(renderManager.getDistanceToCamera(loc.x, loc.y, loc.z));
    double x = loc.x - renderManager.viewerPosX;
    double y = (loc.y - renderManager.viewerPosY) + 1;
    double z = loc.z - renderManager.viewerPosZ;
    double adjustedDistance = distance;
    if (distance > maxDistance) {
      x = x / distance * maxDistance;
      y = y / distance * maxDistance;
      z = z / distance * maxDistance;
      adjustedDistance = maxDistance;
    }

    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y, z);
    GL11.glNormal3f(0F, 1F, 0F);
    GlStateManager.rotate(-renderManager.playerViewY, 0F, 1F, 0F);
    GlStateManager.rotate(renderManager.playerViewX, 1F, 0F, 0F);

    double scaleByFOV = 0.3 + renderManager.options.fovSetting / 100F;
    double scale = (adjustedDistance * 0.1F + 1.0F) * 0.03 * scaleByFOV * configScale;

    GlStateManager.scale(-scale, -scale, scale);

    GlStateManager.disableDepth();
    GlStateManager.enableTexture2D();
    GlStateManager.disableAlpha();
    GlStateManager.blendFunc(770, 771);

    float alphaByAngle = this.alphaByAngle(loc, renderManager);
    float alphaByDistance = this.alphabyDistance((float) distance);
    float alpha = MathHelper.clamp_float(alphaByAngle + alphaByDistance, 0F, 1F);

    FontRenderer font = renderManager.getFontRenderer();
    float w = (float) (font.getStringWidth(dungeonInfo.name) * 0.75);

    if (alpha > 0.1F) {
      Color c = dungeonInfo.getColor();
      GlStateManager.color(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, alpha);
      renderManager.renderEngine.bindTexture(MARKER_BG);
      GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

      GL11.glTexCoord2f(0, 0);
      GL11.glVertex3f(-w, -12.5F, 0F);

      GL11.glTexCoord2f(0, 1);
      GL11.glVertex3f(-w, 12.5F, 0F);

      GL11.glTexCoord2f(1, 0);
      GL11.glVertex3f(w, -12.5F, 0F);

      GL11.glTexCoord2f(1, 1);
      GL11.glVertex3f(w, 12.5F, 0F);

      GL11.glEnd();

      if (font != null) {
        if (adjustedDistance < 10) {
          AdvancedFontRenderer.bigMode = true;
        }
        GlStateManager.scale(0.75f, 0.7, 0.75f);
        this.drawCenteredString(dungeonInfo.name, 0, -6, alpha);
        GlStateManager.scale(0.75f, 0.7, 0.75f);
        this.drawCenteredString("Lv. " + dungeonInfo.difficulty, 0, 4, alpha);

        if (alpha > 0.5F) {
          this.drawCenteredString(String.format("%.1fm", distance), 0, 14, (alpha - 0.5F) * 2F);
        }
        AdvancedFontRenderer.bigMode = false;
      }

    }
    GlStateManager.enableAlpha();
    GlStateManager.enableDepth();
    GlStateManager.popMatrix();
  }

  private float alphabyDistance(float distance) {
    if (distance < distanceAlwaysRender) {
      return 1.0F;
    }

    distance -= distanceAlwaysRender;

    if (distance < maxRenderDistance) {
      return 1F - (distance / maxRenderDistance);
    }

    return 0F;
  }

  private float alphaByAngle(Vector3f loc, RenderManager renderManager) {
    float x = (float) (loc.x - renderManager.viewerPosX);
    float z = (float) (loc.z - renderManager.viewerPosZ);

    float look = (float) Math.toRadians(renderManager.playerViewY);

    float cos = MathHelper.cos(look);
    float sin = MathHelper.sin(look);

    Vector2f locationVec = new Vector2f(z, -x);
    Vector2f viewVec = new Vector2f(cos, sin);

    float angle = (float) Math.toDegrees(Vector2f.angle(locationVec, viewVec));

    return MathHelper.clamp_float(1F - (angle / 15F), 0F, 1F);
  }

  private void drawCenteredString(String text, int x, int y, float alpha) {
    int i = ExtendTheLow.AdvancedFont.getStringWidth(text);
    ExtendTheLow.AdvancedFont.drawString(text, x - i / 2, y, ColorUtil.applyAlpha(0xFFFFFF, alpha));
  }
}
