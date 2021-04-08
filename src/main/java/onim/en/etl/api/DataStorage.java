package onim.en.etl.api;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import onim.en.etl.api.dto.DungeonInfo;
import onim.en.etl.api.dto.PlayerStatus;

public class DataStorage {

  private static HashMap<String, PlayerStatus> nameToStatus = Maps.newHashMap();
  private static HashMap<UUID, PlayerStatus> idToStatus = Maps.newHashMap();

  private static HashSet<DungeonInfo> dungeons = Sets.newHashSet();

  public static void setStatus(PlayerStatus status) {
    if (status == null) {
      return;
    }

    if (status.uuid == null || status.mcid == null) {
      return;
    }

    idToStatus.put(status.uuid, status);
    nameToStatus.put(status.mcid, status);
  }

  public static PlayerStatus getStatusByUniqueId(UUID uniqueId) {
    return idToStatus.get(uniqueId);
  }

  public static PlayerStatus getStatusByName(String name) {
    return nameToStatus.get(name);
  }

  public static void addDungeon(DungeonInfo dungeonInfo) {
    dungeons.add(dungeonInfo);
  }

  public static Set<DungeonInfo> getDungeons() {
    return dungeons;
  }

  public static void clearDungeons() {
    dungeons.clear();
  }

}

