package onim.en.etl.action;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.api.HandleAPI;
import onim.en.etl.extension.ExtensionManager;
import onim.en.etl.extension.TheLowExtension;
import onim.en.etl.util.GuiUtil;

public class QuickActionExecutor {

  private static final Map<String, Runnable> quickActions = Maps.newHashMap();
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

  public static void toggleDungoneMarker() {
    TheLowExtension extension = ExtensionManager.getExtension("onim.en.etl.dungeonMarker");

    if (extension.isEnabled()) {
      ExtensionManager.disableExtension(extension);
    } else {
      ExtensionManager.enableExtension(extension);
    }
  }

  public static void openSettingsGUI() {
    Minecraft.getMinecraft().addScheduledTask(() -> {
      GuiUtil.openSettingGUI();
    });
  }

  public static void requestApiDatas() {
    HandleAPI.requestDatas();
  }

  public static void executeStatsCommand() {
    ExtendTheLow.executeCommand("/stats");
  }

  public static void openQuestGUI() {
    ExtendTheLow.executeCommand("/quest");
  }

  public static void executeNoThrow() {
    ExtendTheLow.executeCommand("/noThrow");
  }
}
