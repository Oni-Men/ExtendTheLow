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

    public int reinCount;

  }

  public static class ClanInfo {

    public String clanId;

    public String clanName;

    public String clanRank;

  }

}
