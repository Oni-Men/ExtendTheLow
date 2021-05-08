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
import onim.en.etl.util.JavaUtil;

public class QuickActionManager {

  private static final List<String> quickActions = Lists.newArrayList();
  static {
    quickActions.addAll(QuickActionExecutor.getBuiltinActions().keySet());
  }

  public static void add(String actionId) {
    quickActions.add(actionId);
  }

  public static void set(int i, String actionId) {
    if (JavaUtil.lengthCheck(quickActions, i)) {
      quickActions.set(i, actionId);
    }
  }

  public static void remove(int i) {
    if (JavaUtil.lengthCheck(quickActions, i)) {
      quickActions.remove(i);
    }
  }

  public static boolean remove(String actionId) {
    return quickActions.remove(actionId);
  }

  public static String[] getActionIds() {
    return quickActions.toArray(new String[quickActions.size()]);
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
