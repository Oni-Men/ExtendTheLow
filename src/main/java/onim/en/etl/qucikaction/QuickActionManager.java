package onim.en.etl.qucikaction;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import onim.en.etl.ExtendTheLow;

public class QuickActionManager {

  private static Map<String, Runnable> quickActions = Maps.newHashMap();
  static {
    quickActions.put("onim.en.etl.quickAction.toggleDungeonMarker", QuickActionExecutor::toggleDungoneMarker);
    quickActions.put("onim.en.etl.quickAction.openSettingsGUI", QuickActionExecutor::openSettingsGUI);
    quickActions.put("onim.en.etl.quickAction.requestApiDatas", QuickActionExecutor::requestApiDatas);
    quickActions.put("onim.en.etl.quickAction.commandQuest", QuickActionExecutor::openQuestGUI);
    quickActions.put("onim.en.etl.quickAction.commandNoThrow", QuickActionExecutor::executeNoThrow);
    quickActions.put("onim.en.etl.quickAction.commandStats", QuickActionExecutor::executeStatsCommand);
  }

  public static void register(String actionId, Runnable action) {
    quickActions.put(actionId, action);
  }

  public static String[] getActionIds() {
    return quickActions.keySet().toArray(new String[quickActions.size()]);
  }

  public static void execute(String actionId) {
    Runnable action = quickActions.get(actionId);
    if (action != null) {
      action.run();
    }
  }

  public static void load() {
    Path path = ExtendTheLow.configPath.resolve("quickActions.json");
    Gson gson = new Gson();
    String json;
    try {
      if (Files.exists(path)) {
        json = Files.lines(path, StandardCharsets.UTF_8).collect(Collectors.joining("\n"));
        quickActions = gson.fromJson(json, new TypeToken<Map<String, Runnable>>() {}.getType());
      } else {
        quickActions = Maps.newHashMap();
        return;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
  }

  public static void save() {
    new Thread(() -> {
      Path path = ExtendTheLow.configPath.resolve("quickActions.json");
      Gson gson = new Gson();
      try {
        Files.write(path, Arrays.asList(gson.toJson(quickActions).split("\n")), StandardCharsets.UTF_8);
      } catch (IOException e) {
        e.printStackTrace();
        return;
      }
    }).start();
  }

}
