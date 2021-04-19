package onim.en.etl.api.dto;

import java.util.UUID;

public class PlayerStatus {

  public UUID uuid;
  
  public String mcid;

  public int mainLevel;

  public SubStatus swordStatus;

  public SubStatus bowStatus;

  public SubStatus magicStatus;

  public ClanInfo clanInfo;

  public long galions;

  public long unit;

  public String jobName;

  public static class SubStatus {

    public int leve;

    public int exp;

    public int maxLevel;

    public int reincCount;

  }

  public static class ClanInfo {

    public String clanId;

    public String clanName;

    public String clanRank;

  }

  public int getReinCount() {
    int i = 0;
    i += swordStatus != null ? swordStatus.reincCount : 0;
    i += magicStatus != null ? magicStatus.reincCount : 0;
    i += bowStatus != null ? bowStatus.reincCount : 0;
    return i;
  }
}
