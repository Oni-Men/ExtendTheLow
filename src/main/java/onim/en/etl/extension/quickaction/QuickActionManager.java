package onim.en.etl.extension.quickaction;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import onim.en.etl.ExtendTheLow;

public class QuickActionManager {

  private static final List<String> quickActions = Lists.newArrayList();
  static {
    quickActions.addAll(QuickActionExecutor.getBuiltinActions().keySet());
  }

  public static void register(String actionId, Runnable action) {
    quickActions.add(actionId);
  }

  public static String[] getActionIds() {
    return quickActions.toArray(new String[quickActions.size()]);
  }

  public static void execute(String actionId) {
    Runnable action = QuickActionExecutor.getBuiltinActions().get(actionId);
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
        quickActions.clear();
        quickActions.addAll(gson.fromJson(json, new TypeToken<List<String>>() {}.getType()));
      } else {
        reset();
        return;
      }
    } catch (Exception e) {
      e.printStackTrace();
      reset();
      return;
    }
  }

  public static void save() {
    new Thread(() -> {
      Path path = ExtendTheLow.configPath.resolve("quickActions.json");
      Gson gson = new Gson();
      try {
        Files.write(path, Arrays.asList(gson.toJson(quickActions).split("\n")), StandardCharsets.UTF_8);
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
    }).start();
  }

  public static void reset() {
    quickActions.clear();
    quickActions.addAll(QuickActionExecutor.getBuiltinActions().keySet());
  }

}
