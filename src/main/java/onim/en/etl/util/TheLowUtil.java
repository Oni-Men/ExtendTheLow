package onim.en.etl.util;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import onim.en.etl.api.DataStorage;
import onim.en.etl.api.dto.PlayerStatus;

public class TheLowUtil {

  private static final String THELOW_SCOREBOARD_TITLE = EnumChatFormatting.AQUA + "===== The Low =====";

  private static final Pattern levelPattern = Pattern.compile("(?<level>\\d+)レベル");
  private static final Pattern galionPattern = Pattern.compile("(?<galion>\\d+) G");

  private static final HashMap<String, BiConsumer<PlayerStatus, String>> pareRules = Maps.newHashMap();
  static {
    BiConsumer<PlayerStatus, String> levelRule = (playerStatus, s) -> {
      playerStatus.mainLevel = getLevel(s);
    };

    pareRules.put("メインレベル", levelRule);
    pareRules.put("剣術", levelRule);
    pareRules.put("魔術", levelRule);
    pareRules.put("弓術", levelRule);

    pareRules.put("お金", (playerStatus, s) -> {
      playerStatus.galions = getGalion(s);
    });

    pareRules.put("ユニット", (playerStatus, s) -> {
      playerStatus.unit = JavaUtil.parseInt(s, 0);
    });
  }

  private static int getLevel(String levelString) {
    Matcher matcher = levelPattern.matcher(levelString);
    if (matcher.matches()) {
      return JavaUtil.parseInt(matcher.group("level"), 0);
    }
    return 0;
  }

  private static int getGalion(String galionString) {
    Matcher matcher = galionPattern.matcher(galionString);
    if (matcher.matches()) {
      return JavaUtil.parseInt(matcher.group("galion"), 0);
    }
    return 0;
  }

  public static boolean isPlayingTheLow() {
    Minecraft minecraft = Minecraft.getMinecraft();
    ServerData currentServerData = minecraft.getCurrentServerData();

    if (currentServerData == null) {
      return false;
    }

    // if (!currentServerData.serverIP.equalsIgnoreCase("mc.eximradar.jp")) {
    // return false;
    // }

    WorldClient world = minecraft.theWorld;

    if (world == null) {
      return false;
    }

    Scoreboard scoreboard = world.getScoreboard();

    if (scoreboard == null) {
      return false;
    }

    ScoreObjective displaySlot = scoreboard.getObjectiveInDisplaySlot(1);

    if (displaySlot == null) {
      return false;
    }

    return THELOW_SCOREBOARD_TITLE.equals(displaySlot.getDisplayName());
  }

  public static void applyPlayerStatus(ScoreObjective scoreObjective) {
    Minecraft mc = Minecraft.getMinecraft();
    PlayerStatus playerStatus = DataStorage.getStatusByUniqueId(mc.thePlayer.getUniqueID());

    if (playerStatus == null) {
      return;
    }

    Scoreboard scoreboard = scoreObjective.getScoreboard();
    for (Score score : scoreboard.getScores()) {
      String text = EnumChatFormatting.getTextWithoutFormattingCodes(score.getPlayerName());

      if (text.indexOf(":") == -1)
        continue;

      String[] pare = text.split(":");
      BiConsumer<PlayerStatus, String> rule = pareRules.get(pare[0].trim());
      if (rule != null) {
        rule.accept(playerStatus, pare[1].trim());
      }
    }
  }

  public static String formatGalions(long galion) {
    if (galion < 1000) {
      return galion + "G";
    }
    return String.format("%.2fkG", (galion / 1000F));
  }

  public static String formatPlayerName(PlayerStatus status) {
    if (status.clanInfo == null) {
      return status.mcid;
    }
    return String.format("%s§c[%s]", status.mcid, status.clanInfo.clanName);
  }

  public static String formatCooltime(float sec) {
    StringBuilder builder = new StringBuilder();

    if (sec < 0) {
      builder.append("Ready!");
    } else {
      builder.append(JavaUtil.padding(String.valueOf((int) sec / 60), "00"));
      builder.append(":");

      builder.append(JavaUtil.padding(String.valueOf((int) sec % 60), "00"));
    }

    return builder.toString();
  }
}
