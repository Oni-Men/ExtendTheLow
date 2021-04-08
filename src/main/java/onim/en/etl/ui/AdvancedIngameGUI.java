package onim.en.etl.ui;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import onim.en.etl.api.DataStorage;
import onim.en.etl.api.dto.PlayerStatus;
import onim.en.etl.util.TheLowUtil;

public class AdvancedIngameGUI extends GuiIngameForge {

  private static final ResourceLocation TEX_SWORD =
      new ResourceLocation("onim.en.etl:textures/sword.png");
  private static final ResourceLocation TEX_WAND =
      new ResourceLocation("onim.en.etl:textures/wand.png");
  private static final ResourceLocation TEX_BOW =
      new ResourceLocation("onim.en.etl:textures/bow.png");

  public AdvancedIngameGUI(Minecraft mc) {
    super(mc);
  }

  @Override
  protected void renderTitle(int width, int height, float partialTicks) {
    AdvancedFontRenderer.bigMode = true;
    super.renderTitle(width, height, partialTicks);
    AdvancedFontRenderer.bigMode = false;
  }

  @Override
  protected void renderScoreboard(ScoreObjective scoreObjective,
      ScaledResolution scaledResolution) {
    if (TheLowUtil.isPlayingTheLow()) {
      TheLowUtil.applyPlayerStatus(scoreObjective);
      this.renderTheLowStatus(scaledResolution);
      return;
    }
    Scoreboard scoreboard = scoreObjective.getScoreboard();
    Collection<Score> collection = scoreboard.getSortedScores(scoreObjective);
    List<Score> list = Lists.newArrayList(Iterables.filter(collection, new Predicate<Score>() {
      public boolean apply(Score p_apply_1_) {
        return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
      }
    }));

    if (list.size() > 15) {
      collection = Lists.newArrayList(Iterables.skip(list, collection.size() - 15));
    } else {
      collection = list;
    }

    int i = this.getFontRenderer().getStringWidth(scoreObjective.getDisplayName());

    for (Score score : collection) {
      ScorePlayerTeam scoreplayerteam = scoreboard.getPlayersTeam(score.getPlayerName());
      String s = ScorePlayerTeam.formatPlayerName(scoreplayerteam, score.getPlayerName()) + ": "
          + EnumChatFormatting.RED + score.getScorePoints();
      i = Math.max(i, this.getFontRenderer().getStringWidth(s));
    }

    int i1 = collection.size() * this.getFontRenderer().FONT_HEIGHT;
    int j1 = scaledResolution.getScaledHeight() / 2 + i1 / 3;
    int k1 = 3;
    int l1 = scaledResolution.getScaledWidth() - i - k1;
    int j = 0;

    for (Score score1 : collection) {
      ++j;
      ScorePlayerTeam scoreplayerteam1 = scoreboard.getPlayersTeam(score1.getPlayerName());
      String s1 = ScorePlayerTeam.formatPlayerName(scoreplayerteam1, score1.getPlayerName());
      String s2 = EnumChatFormatting.RED + "" + score1.getScorePoints();
      int k = j1 - j * this.getFontRenderer().FONT_HEIGHT;
      int l = scaledResolution.getScaledWidth() - k1 + 2;
      drawRect(l1 - 2, k, l, k + this.getFontRenderer().FONT_HEIGHT, 1342177280);
      this.getFontRenderer().drawString(s1, l1, k, 0xffffffff);
      this.getFontRenderer()
          .drawString(s2, l - this.getFontRenderer().getStringWidth(s2), k, 0xffffffff);

      if (j == collection.size()) {
        String s3 = scoreObjective.getDisplayName();
        drawRect(l1 - 2, k - this.getFontRenderer().FONT_HEIGHT - 1, l, k - 1, 1610612736);
        drawRect(l1 - 2, k - 1, l, k, 1342177280);
        this.getFontRenderer()
            .drawString(s3, l1 + i / 2 - this.getFontRenderer().getStringWidth(s3) / 2,
                k - this.getFontRenderer().FONT_HEIGHT, 0xffffffff);
      }
    }
  }

  public void renderTheLowStatus(ScaledResolution scaledResolution) {
    Minecraft mc = Minecraft.getMinecraft();
    Entity entity = mc.getRenderViewEntity();

    if (!(entity instanceof AbstractClientPlayer)) {
      return;
    }

    AbstractClientPlayer player = (AbstractClientPlayer) entity;
    PlayerStatus playerStatus = DataStorage.getStatusByUniqueId(player.getUniqueID());

    drawGradientRectHorizontal(0, 0, 60, 30, 0xAA000000, 0xAA000000);
    drawGradientRectHorizontal(60, 0, 120, 30, 0xAA000000, 0x00000000);

    mc.getTextureManager().bindTexture(player.getLocationSkin());
    GlStateManager.pushMatrix();
    GlStateManager.translate(4, 4, 0);
    GlStateManager.scale(0.8, 0.8, 1.0);
    drawScaledCustomSizeModalRect(0, 0, 8, 8, 8, 8, 16, 16, 64, 64);
    GlStateManager.scale(1.1, 1.1, 1.0);
    GlStateManager.translate(-0.8, -0.8, 0);
    drawScaledCustomSizeModalRect(0, 0, 40, 8, 8, 8, 16, 16, 64, 64);
    GlStateManager.popMatrix();

    if (playerStatus != null) {
      this.getFontRenderer()
          .drawStringWithShadow("Lv." + playerStatus.mainLevel, 20, 2, 0xFFFFFF);
      this.getFontRenderer()
          .drawStringWithShadow(TheLowUtil.formatPlayerName(playerStatus), 20, 10, 0xFFFFFF);

      GlStateManager.color(1F, 1F, 1F);
      GlStateManager.pushMatrix();
      GlStateManager.translate(45, 0, 0);

      float f = (Minecraft.getSystemTime() % 9000) / 1000F;

      if (f < 3) {
        drawIcon(TEX_SWORD, 0, 2, 8, 8);
        String s = String.format("(lv. %d)", playerStatus.swordStatus.leve);
        this.getFontRenderer().drawStringWithShadow(s, 10, 2, 0xFFFFFF);
      } else if (f < 6) {
        drawIcon(TEX_WAND, 0, 2, 8, 8);
        String s = String.format("(lv. %d)", playerStatus.magicStatus.leve);
        this.getFontRenderer().drawStringWithShadow(s, 10, 2, 0xFFFFFF);
      } else {
        drawIcon(TEX_BOW, 0, 2, 8, 8);
        String s = String.format("(lv. %d)", playerStatus.bowStatus.leve);
        this.getFontRenderer().drawStringWithShadow(s, 10, 2, 0xFFFFFF);
      }

      GlStateManager.popMatrix();

      this.getFontRenderer()
          .drawString(TheLowUtil.formatGalions(playerStatus.galions), 4, 18, 0xFFFFFF);

      this.getFontRenderer()
          .drawString(String.format("%d Units", playerStatus.unit), 45, 18, 0xFFFFFF);
    } else {
      this.getFontRenderer().drawStringWithShadow(player.getDisplayNameString(), 20, 6, 0xFFFFFF);
    }
  }

  private void drawIcon(ResourceLocation iconLocation, int x, int y, int w, int h) {
    Minecraft mc = Minecraft.getMinecraft();
    mc.getTextureManager().bindTexture(iconLocation);
    drawScaledCustomSizeModalRect(x, y, 0, 0, 32, 32, w, h, 32, 32);
  }

  public void drawGradientRectHorizontal(int left, int top, int right, int bottom, int startColor,
      int endColor) {
    float alpha1 = (float) (startColor >> 24 & 255) / 255.0F;
    float red1 = (float) (startColor >> 16 & 255) / 255.0F;
    float green1 = (float) (startColor >> 8 & 255) / 255.0F;
    float blue1 = (float) (startColor & 255) / 255.0F;
    float alpha2 = (float) (endColor >> 24 & 255) / 255.0F;
    float red2 = (float) (endColor >> 16 & 255) / 255.0F;
    float green2 = (float) (endColor >> 8 & 255) / 255.0F;
    float blue2 = (float) (endColor & 255) / 255.0F;
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.shadeModel(7425);
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
    worldrenderer.func_181662_b((double) right, (double) top, (double) this.zLevel)
        .func_181666_a(red2, green2, blue2, alpha2)
        .func_181675_d();
    worldrenderer.func_181662_b((double) left, (double) top, (double) this.zLevel)
        .func_181666_a(red1, green1, blue1, alpha1)
        .func_181675_d();
    worldrenderer.func_181662_b((double) left, (double) bottom, (double) this.zLevel)
        .func_181666_a(red1, green1, blue1, alpha1)
        .func_181675_d();
    worldrenderer.func_181662_b((double) right, (double) bottom, (double) this.zLevel)
        .func_181666_a(red2, green2, blue2, alpha2)
        .func_181675_d();
    tessellator.draw();
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
  }
}
