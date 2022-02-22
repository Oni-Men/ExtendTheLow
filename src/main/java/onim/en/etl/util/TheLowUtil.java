package onim.en.etl.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.EnumChatFormatting;
import onim.en.etl.Prefs;
import onim.en.etl.api.dto.PlayerStatus;
import onim.en.etl.api.dto.PlayerStatus.ClanInfo;

import java.text.DecimalFormat;


public class TheLowUtil {

  private static final String THELOW_SCOREBOARD_TITLE = EnumChatFormatting.AQUA + "===== The Low =====";

  public static boolean isPlayingTheLow() {
    Minecraft minecraft = Minecraft.getMinecraft();
    ServerData currentServerData = minecraft.getCurrentServerData();

    if (currentServerData == null) {
      return false;
    }

    if (!Prefs.get().debugMode) {
      if (!currentServerData.serverIP.equalsIgnoreCase("mc.eximradar.jp")) {
        return false;
      }
    }

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
  static DecimalFormat df1 = new DecimalFormat("#,###,###");

  public static String formatGalions(long galion) {
    if (galion < 1000) {
      return galion + " Galion";
    }
    return String.format("%s Galion", df1.format(galion));
  }

  public static String formatPlayerName(PlayerStatus status) {
    // * if (status.clanInfo == null) {
    // *   return status.mcid;
    // * }
    return String.format("%s %s", status.mcid, formatClanName(status.clanInfo)); //
  }

  public static String formatClanName(ClanInfo clanInfo) {
    return ClanUtil.formatClanName(clanInfo);
  }
  // `職業` 追加
  public static String formatJobName(String jobName) {
    return String.format("%s", jobName);
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
