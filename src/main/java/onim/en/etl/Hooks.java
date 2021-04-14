package onim.en.etl;

import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import onim.en.etl.event.GetCharWidthEvent;
import onim.en.etl.event.RenderCharAtPosEvent;

public class Hooks {

  public static float onGetCharWidth(float width, char ch) {
    GetCharWidthEvent event = new GetCharWidthEvent(ch, width);
    MinecraftForge.EVENT_BUS.post(event);
    return event.getWidth();
  }

  public static void onRenderCharAtPos(boolean boldStyle) {
    MinecraftForge.EVENT_BUS.post(new RenderCharAtPosEvent(boldStyle));
  }

  public static boolean onHandleTeams(S3EPacketTeams packetIn, Scoreboard scoreboard) {
    ScorePlayerTeam scoreplayerteam;
    if (packetIn.func_149307_h() == 0) {
      scoreplayerteam = scoreboard.createTeam(packetIn.func_149312_c());
    } else {
      scoreplayerteam = scoreboard.getTeam(packetIn.func_149312_c());
    }

    if (packetIn.func_149307_h() == 0 || packetIn.func_149307_h() == 2) {

      if (scoreplayerteam != null) {

        scoreplayerteam.setTeamName(packetIn.func_149306_d());
        scoreplayerteam.setNamePrefix(packetIn.func_149311_e());
        scoreplayerteam.setNameSuffix(packetIn.func_149309_f());
        scoreplayerteam.setChatFormat(EnumChatFormatting.func_175744_a(packetIn.func_179813_h()));
        scoreplayerteam.func_98298_a(packetIn.func_149308_i());
        Team.EnumVisible team$enumvisible = Team.EnumVisible.func_178824_a(packetIn.func_179814_i());

        if (team$enumvisible != null) {
          scoreplayerteam.setNameTagVisibility(team$enumvisible);
        }
      }
    }

    if (packetIn.func_149307_h() == 0 || packetIn.func_149307_h() == 3) {
      for (String s : packetIn.func_149310_g()) {
        scoreboard.addPlayerToTeam(s, packetIn.func_149312_c());
      }
    }

    if (packetIn.func_149307_h() == 4) {
      for (String s1 : packetIn.func_149310_g()) {
        scoreboard.removePlayerFromTeam(s1, scoreplayerteam);
      }
    }

    if (packetIn.func_149307_h() == 1) {
      if (scoreplayerteam != null) {
        scoreboard.removeTeam(scoreplayerteam);
      }
    }

    return true;
  }

  public static float getShadowOffset(float offset) {
    if (Prefs.get().betterFont) {
      return .5F;
    }
    return offset;
  }

  public static float getShadowOffsetMinus(float offset) {
    if (Prefs.get().betterFont) {
      return 0F;
    }
    return offset;
  }
}
