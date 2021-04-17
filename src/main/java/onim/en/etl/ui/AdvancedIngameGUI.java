package onim.en.etl.ui;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.Prefs;
import onim.en.etl.api.DataStorage;
import onim.en.etl.api.dto.PlayerStatus;
import onim.en.etl.util.GuiUtil;
import onim.en.etl.util.TheLowUtil;

public class AdvancedIngameGUI extends GuiIngameForge {

  private static final ResourceLocation TEX_SWORD = new ResourceLocation("onim.en.etl:textures/sword.png");
  private static final ResourceLocation TEX_WAND = new ResourceLocation("onim.en.etl:textures/wand.png");
  private static final ResourceLocation TEX_BOW = new ResourceLocation("onim.en.etl:textures/bow.png");

  public AdvancedIngameGUI(Minecraft mc) {
    super(mc);
  }

  @Override
  protected void renderTitle(int width, int height, float partialTicks) {
    AdvancedFontRenderer.bigMode = true;

    if (field_175195_w > 0) {
      mc.mcProfiler.startSection("titleAndSubtitle");
      float age = (float) this.field_175195_w - partialTicks;
      int opacity = 255;

      if (field_175195_w > field_175193_B + field_175192_A) {
        float f3 = (float) (field_175199_z + field_175192_A + field_175193_B) - age;
        opacity = (int) (f3 * 255.0F / (float) field_175199_z);
      }
      if (field_175195_w <= field_175193_B)
        opacity = (int) (age * 255.0F / (float) this.field_175193_B);

      opacity = MathHelper.clamp_int(opacity, 0, 255);

      if (opacity > 8) {
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) (width / 2), (float) (height / 2), 0.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        GlStateManager.scale(4.0F, 4.0F, 4.0F);

        int l = opacity << 24 & -16777216;
        float textWidth = ExtendTheLow.AdvancedFont.getStringWidth(field_175201_x);
        this.getFontRenderer().drawString(this.field_175201_x, -textWidth / 2F + 2.5F, -10, 16777215 | l, true);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        textWidth = ExtendTheLow.AdvancedFont.getStringWidth(field_175200_y);

        this.getFontRenderer().drawString(this.field_175200_y, -textWidth / 2F + 2.5F, 5.0F, 16777215 | l, true);
        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
      }

      this.mc.mcProfiler.endSection();
    }

    AdvancedFontRenderer.bigMode = false;
  }

  @Override
  protected void renderBossHealth() {
    Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.icons);
    super.renderBossHealth();
  }

  @Override
  protected void renderScoreboard(ScoreObjective scoreObjective, ScaledResolution scaledResolution) {
    if (TheLowUtil.isPlayingTheLow() && Prefs.get().customTheLowStatus) {
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
      this.getFontRenderer().drawString(s2, l - this.getFontRenderer().getStringWidth(s2), k, 0xffffffff);

      if (j == collection.size()) {
        String s3 = scoreObjective.getDisplayName();
        drawRect(l1 - 2, k - this.getFontRenderer().FONT_HEIGHT - 1, l, k - 1, 1610612736);
        drawRect(l1 - 2, k - 1, l, k, 1342177280);
        this.getFontRenderer().drawString(s3, l1 + i / 2 - this.getFontRenderer().getStringWidth(s3) / 2,
            k - this.getFontRenderer().FONT_HEIGHT, 0xffffffff);
      }
    }
  }

  public void renderTheLowStatus(ScaledResolution scaledResolution) {
    Minecraft mc = Minecraft.getMinecraft();
    Entity entity = mc.getRenderViewEntity();

    if (this.mc.gameSettings.showDebugInfo) {
      return;
    }

    if (!(entity instanceof AbstractClientPlayer)) {
      return;
    }

    AbstractClientPlayer player = (AbstractClientPlayer) entity;
    PlayerStatus playerStatus = DataStorage.getStatusByUniqueId(player.getUniqueID());

    int i = scaledResolution.getScaledWidth();
    boolean right = Prefs.get().invertTheLowStatus;

    if (right) {
      GuiUtil.drawGradientRectHorizontal(i - 120, 0, i - 60, 30, 0x00000000, 0xAA000000);
      GuiUtil.drawGradientRectHorizontal(i - 60, 0, i, 30, 0xAA000000, 0xAA000000);
    } else {
      GuiUtil.drawGradientRectHorizontal(0, 0, 60, 30, 0xAA000000, 0xAA000000);
      GuiUtil.drawGradientRectHorizontal(60, 0, 120, 30, 0xAA000000, 0x00000000);
    }

    GlStateManager.pushMatrix();
    GlStateManager.translate(right ? i - 116 : 4, 4, 0);
    GlStateManager.color(0.3F, 0.3F, 0.3F);
    this.drawFace(player.getLocationSkin(), 1, 1);
    GlStateManager.color(1.0F, 1.0F, 1.0F);
    this.drawFace(player.getLocationSkin(), 0, 0);
    GlStateManager.popMatrix();

    GlStateManager.pushMatrix();
    GlStateManager.translate(right ? i - 120 : 0, 0, 0);
    if (playerStatus != null) {

      this.getFontRenderer().drawStringWithShadow("Lv." + playerStatus.mainLevel, 20, 2, 0xFFFFFF);
      this.getFontRenderer().drawStringWithShadow(TheLowUtil.formatPlayerName(playerStatus), 20, 10, 0xFFFFFF);

      float f = (Minecraft.getSystemTime() % 9000) / 1000F;

      GlStateManager.color(1F, 1F, 1F);

      if (f < 3) {
        drawIcon(TEX_SWORD, 45, 2, 8, 8);
        String s = String.format("(lv. %d)", playerStatus.swordStatus.leve);
        this.getFontRenderer().drawStringWithShadow(s, 55, 2, 0xFFFFFF);
      } else if (f < 6) {
        drawIcon(TEX_WAND, 45, 2, 8, 8);
        String s = String.format("(lv. %d)", playerStatus.magicStatus.leve);
        this.getFontRenderer().drawStringWithShadow(s, 55, 2, 0xFFFFFF);
      } else {
        drawIcon(TEX_BOW, 45, 2, 8, 8);
        String s = String.format("(lv. %d)", playerStatus.bowStatus.leve);
        this.getFontRenderer().drawStringWithShadow(s, 55, 2, 0xFFFFFF);
      }

      this.getFontRenderer().drawString(TheLowUtil.formatGalions(playerStatus.galions), 4, 18, 0xFFFFFF);

      this.getFontRenderer().drawString(String.format("%d Units", playerStatus.unit), 55, 18, 0xFFFFFF);

    } else {
      this.getFontRenderer().drawStringWithShadow(player.getDisplayNameString(), 20, 6, 0xFFFFFF);
    }
    GlStateManager.popMatrix();
  }

  private void drawIcon(ResourceLocation iconLocation, int x, int y, int w, int h) {
    Minecraft mc = Minecraft.getMinecraft();
    mc.getTextureManager().bindTexture(iconLocation);
    drawScaledCustomSizeModalRect(x, y, 0, 0, 32, 32, w, h, 32, 32);
  }

  private void drawFace(ResourceLocation locationSkin, float x, float y) {
    mc.getTextureManager().bindTexture(locationSkin);
    GlStateManager.pushMatrix();
    GlStateManager.translate(x, y, 0);
    GlStateManager.scale(0.8, 0.8, 1.0);
    drawScaledCustomSizeModalRect(0, 0, 8, 8, 8, 8, 16, 16, 64, 64);
    GlStateManager.scale(1.1, 1.1, 1.0);
    GlStateManager.translate(-0.8, -0.8, 0);
    drawScaledCustomSizeModalRect(0, 0, 40, 8, 8, 8, 16, 16, 64, 64);
    GlStateManager.popMatrix();

  }

}
