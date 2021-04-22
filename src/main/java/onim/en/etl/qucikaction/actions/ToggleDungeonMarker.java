package onim.en.etl.qucikaction.actions;

import onim.en.etl.extension.ExtensionManager;
import onim.en.etl.extension.TheLowExtension;
import onim.en.etl.qucikaction.CustomAction;

public class ToggleDungeonMarker extends CustomAction {

  public ToggleDungeonMarker() {
    super("onim.en.etl.quickAction.toggleDungeonMarker");
  }

  @Override
  public void execute() {
    TheLowExtension extension = ExtensionManager.getExtension("onim.en.etl.dungeonMarker");

    if (extension.isEnabled()) {
      ExtensionManager.disableExtension(extension);
    } else {
      ExtensionManager.enableExtension(extension);
    }
  }

}
