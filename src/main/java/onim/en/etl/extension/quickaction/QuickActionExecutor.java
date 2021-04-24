package onim.en.etl.extension.quickaction;

import net.minecraft.client.Minecraft;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.api.HandleAPI;
import onim.en.etl.extension.ExtensionManager;
import onim.en.etl.extension.TheLowExtension;
import onim.en.etl.util.GuiUtil;

public class QuickActionExecutor {

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
