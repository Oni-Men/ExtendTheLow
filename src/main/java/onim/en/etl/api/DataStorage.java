package onim.en.etl.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import onim.en.etl.ExtendTheLow;
import onim.en.etl.api.dto.DungeonInfo;
import onim.en.etl.api.dto.PlayerStatus;
import onim.en.etl.api.dto.PlayerStatusProvider;

public class DataStorage {

  private static PlayerStatusProvider playerStatusProvider = new PlayerStatusProvider();
  private static HashSet<DungeonInfo> dungeons = Sets.newHashSet();

  public static void setStatus(PlayerStatus status) {
    playerStatusProvider.setStatus(status);
  }

  public static PlayerStatus getStatusByUniqueId(UUID uniqueId) {
    return playerStatusProvider.getStatusByUniqueId(uniqueId);
  }

  public static PlayerStatus getStatusByName(String name) {
    return playerStatusProvider.getStatusByName(name);
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


  public static void load() {
    Gson gson = new GsonBuilder().create();
    Path statusesPath = ExtendTheLow.configPath.resolve("dataStorage.statuses.json");
    Path dungeonsPath = ExtendTheLow.configPath.resolve("dataStorage.dungeons.json");

    try {
      if (Files.exists(statusesPath)) {
        playerStatusProvider =
            gson.fromJson(Files.newBufferedReader(statusesPath), PlayerStatusProvider.class);
      }

      if (Files.exists(dungeonsPath)) {
        dungeons = gson.fromJson(Files.newBufferedReader(dungeonsPath),
            new TypeToken<HashSet<DungeonInfo>>() {}.getType());
      }
    } catch (JsonSyntaxException | JsonIOException | IOException e) {
      e.printStackTrace();
    }
  }

  public static void cache() {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Path statusesPath = ExtendTheLow.configPath.resolve("dataStorage.statuses.json");
    Path dungeonsPath = ExtendTheLow.configPath.resolve("dataStorage.dungeons.json");

    try {
      Files.write(statusesPath, Arrays.asList(gson.toJson(playerStatusProvider).split("\n")),
          StandardCharsets.UTF_8);
      Files.write(dungeonsPath, Arrays.asList(gson.toJson(dungeons).split("\n")),
          StandardCharsets.UTF_8);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
