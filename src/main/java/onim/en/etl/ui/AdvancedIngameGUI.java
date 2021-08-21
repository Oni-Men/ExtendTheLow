package onim.en.etl.ui;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
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
  public void renderGameOverlay(float partialTicks) {
    super.renderGameOverlay(partialTicks);
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
        float textWidth = this.getFontRenderer().getStringWidth(field_175201_x);
        this.getFontRenderer().drawString(this.field_175201_x, -textWidth / 2F + 2.5F, -10, 16777215 | l, true);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.scale(2.0F, 2.0F, 2.0F);
        textWidth = this.getFontRenderer().getStringWidth(field_175200_y);

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
  public void renderHealth(int width, int height) {
    if (!Prefs.get().smartHealthBar) {
      super.renderHealth(width, height);
      return;
    }
    
    mc.getTextureManager().bindTexture(icons);
    mc.mcProfiler.startSection("health");
    GlStateManager.enableBlend();

    EntityPlayer player = (EntityPlayer) this.mc.getRenderViewEntity();
    int health = MathHelper.ceiling_float_int(player.getHealth());
    boolean highlight = healthUpdateCounter > (long) updateCounter
        && (healthUpdateCounter - (long) updateCounter) / 3L % 2L == 1L;

    if (health < this.playerHealth && player.hurtResistantTime > 0) {
      this.lastSystemTime = Minecraft.getSystemTime();
      this.healthUpdateCounter = (long) (this.updateCounter + 20);
    } else if (health > this.playerHealth && player.hurtResistantTime > 0) {
      this.lastSystemTime = Minecraft.getSystemTime();
      this.healthUpdateCounter = (long) (this.updateCounter + 10);
    }

    if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
      this.playerHealth = health;
      this.lastPlayerHealth = health;
      this.lastSystemTime = Minecraft.getSystemTime();
    }

    this.playerHealth = health;
    float maxHealth = player.getMaxHealth();
    int healthLast = this.lastPlayerHealth;
    float absorb = player.getAbsorptionAmount();

    this.rand.setSeed((long) (updateCounter * 312871));

    int left = width / 2 - 91;
    int top = height - left_height;
    left_height += 10;

    int regen = -1;
    if (player.isPotionActive(Potion.regeneration)) {
      regen = updateCounter % 25;
    }

    final int TOP = 9 * (mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
    final int BACKGROUND = (highlight ? 25 : 16);
    int MARGIN = 16;
    if (player.isPotionActive(Potion.poison))
      MARGIN += 36;
    else if (player.isPotionActive(Potion.wither))
      MARGIN += 72;

    float maxAmount = maxHealth + absorb;
    float absorbAmount = absorb / maxAmount;
    float healthAmount = health / maxAmount;

    int x = left;

    for (int i = 0; i < 10; i++) {
      int y = top;
      if (health <= 4)
        y += rand.nextInt(2);
      if (i == regen)
        y -= 2;

      GlStateManager.pushMatrix();
      GlStateManager.translate(0, 0, -10);
      drawTexturedModalRect(left + i * 8, y, BACKGROUND, TOP, 9, 9);
      GlStateManager.popMatrix();

      if (highlight) {
        if (i * 2 + 1 < healthLast)
          drawTexturedModalRect(left + i * 8, y, MARGIN + 54, TOP, 9, 9); // 6
        else if (i * 2 + 1 == healthLast)
          drawTexturedModalRect(left + i * 8, y, MARGIN + 63, TOP, 9, 9); // 7
      }

      if (healthAmount > 0.0F) {

        if (healthAmount >= 0.1F) {
          drawTexturedModalRect(x, y, MARGIN + 36, TOP, 9, 9); // 5
          healthAmount -= 0.1F;
          x += 8;
        } else {
          int w = (int) (healthAmount * 90F);
          drawTexturedModalRect(x, y, MARGIN + 36, TOP, w, 9); // 5
          x += w;
          healthAmount = 0.0F;
        }

      }

      if (healthAmount <= 0F) {
        if (absorbAmount > 0.0F) {
          int mod = (x - left) % 8;
          if (mod == 0) {
            drawTexturedModalRect(x, y, MARGIN + 144, TOP, 9, 9); // 16
            x += 8;
            absorbAmount -= 0.1F;
          } else {
            drawTexturedModalRect(x, y, MARGIN + 144 + mod, TOP, 9 - mod, 9); // 16
            x += 8 - mod;
            absorbAmount -= (8 - mod) / 80F;
          }
        }
      }
    }

    GlStateManager.disableBlend();

    GlStateManager.pushMatrix();
    GlStateManager.translate(width / 2 - 50, height - 37, 0);
    GlStateManager.scale(0.5F, 0.5F, 1.0F);

    String absorbText = absorb == 0F ? "" : ChatFormatting.YELLOW + String.format(" + %d", (int) absorb);
    String healthText = String.format("%d/%d%s", Math.round(player.getHealth()), Math.round(maxHealth), absorbText);
    this.getFontRenderer()
      .drawStringWithShadow(healthText, -this.getFontRenderer().getStringWidth(healthText) / 2, 0, 0xFFFFFF);

    GlStateManager.popMatrix();
    GlStateManager.color(1F, 1F, 1F, 1F);

    mc.getTextureManager().bindTexture(icons);
    mc.mcProfiler.endSection();
  }

  @Override
  protected void renderScoreboard(ScoreObjective scoreObjective, ScaledResolution scaledResolution) {
    if (TheLowUtil.isPlayingTheLow() && Prefs.get().customTheLowStatus) {
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
        this.getFontRenderer().drawString(s3, l1 + i / 2 - this.getFontRenderer().getStringWidth(s3) / 2, k
            - this.getFontRenderer().FONT_HEIGHT, 0xffffffff);
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

    FontRenderer font = ExtendTheLow.AdvancedFont;

    int i = scaledResolution.getScaledWidth();
    boolean right = Prefs.get().invertTheLowStatus;

    if (right) {
      GuiUtil.drawGradientRectHorizontal(i - 120, 0, i - 60, 32, 0x00336633, 0xAA336666);
      GuiUtil.drawGradientRectHorizontal(i - 60, 0, i, 32, 0xAA336666, 0xAA336699);
    } else {
      GuiUtil.drawGradientRectHorizontal(0, 0, 60, 32, 0xAA336699, 0xAA336666);
      GuiUtil.drawGradientRectHorizontal(60, 0, 120, 32, 0xAA336666, 0x00336633);
    }

    GlStateManager.pushMatrix();
    GlStateManager.translate(right ? i - 116 : 4, 4, 0);
    GlStateManager.color(0.3F, 0.3F, 0.4F);
    this.drawFace(player.getLocationSkin(), 1, 1);
    GlStateManager.color(1.0F, 1.0F, 1.0F);
    this.drawFace(player.getLocationSkin(), 0, 0);
    GlStateManager.popMatrix();

    GlStateManager.pushMatrix();
    GlStateManager.translate(right ? i - 120 : 0, 0, 0);
    if (playerStatus != null) {

      int widthLevel = font.drawStringWithShadow("Lv." + playerStatus.mainLevel, 22, 2, 0xFFFFFF);
      font.drawStringWithShadow(TheLowUtil.formatPlayerName(playerStatus), 22, 12, 0xFFFFFF);

      float f = (Minecraft.getSystemTime() % 9000) / 1000F;

      GlStateManager.color(1F, 1F, 1F);

      GlStateManager.pushMatrix();
      GlStateManager.translate(widthLevel + 4, 0, 0);

      if (f < 3) {
        drawIcon(TEX_SWORD, 2, 2, 8, 8);
        String s = String.format("(lv. %d)", playerStatus.swordStatus.leve);
        font.drawStringWithShadow(s, 12, 2, 0xFFFFFF);
      } else if (f < 6) {
        drawIcon(TEX_WAND, 2, 2, 8, 8);
        String s = String.format("(lv. %d)", playerStatus.magicStatus.leve);
        font.drawStringWithShadow(s, 12, 2, 0xFFFFFF);
      } else {
        drawIcon(TEX_BOW, 2, 2, 8, 8);
        String s = String.format("(lv. %d)", playerStatus.bowStatus.leve);
        font.drawStringWithShadow(s, 12, 2, 0xFFFFFF);
      }

      GlStateManager.popMatrix();

      int widthGalions = font
        .drawStringWithShadow(TheLowUtil.formatGalions(playerStatus.galions), 4, 22, 0xFFFFFF);

      font
        .drawStringWithShadow(String.format("%d Units", playerStatus.unit), widthGalions + 4, 22, 0xFFFFFF);

    } else {
      font.drawStringWithShadow(player.getDisplayNameString(), 20, 6, 0xFFFFFF);
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
