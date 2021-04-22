package onim.en.etl.util;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.util.EnumChatFormatting;
import onim.en.etl.api.dto.PlayerStatus.ClanInfo;

public class ClanUtil {

  private static final Map<String, EnumChatFormatting> rankToColors = Maps.newHashMap();
  static {
    rankToColors.put("UNRANKED", EnumChatFormatting.GRAY);
    rankToColors.put("IRON", EnumChatFormatting.GRAY);
    rankToColors.put("GOLD", EnumChatFormatting.GOLD);
    rankToColors.put("LAPIS", EnumChatFormatting.DARK_AQUA);
    rankToColors.put("EMERALD", EnumChatFormatting.DARK_GREEN);
    rankToColors.put("REDSTONE", EnumChatFormatting.DARK_RED);
    rankToColors.put("DIAMOND", EnumChatFormatting.DARK_AQUA);
    rankToColors.put("アメジスト", EnumChatFormatting.DARK_PURPLE);
    rankToColors.put("RUBY", EnumChatFormatting.RED);
    rankToColors.put("サファイア", EnumChatFormatting.BLUE);
    rankToColors.put("ペリドット", EnumChatFormatting.GREEN);
    rankToColors.put("シトリン", EnumChatFormatting.YELLOW);
  }

  public static String formatClanName(ClanInfo clanInfo) {
    if (clanInfo == null) {
      return "";
    }
    EnumChatFormatting chatColor = rankToColors.getOrDefault(clanInfo.clanRank.split(" ")[0], EnumChatFormatting.RESET);
    return String.format("%s[%s]§r", chatColor, clanInfo.clanName);
  }

}
