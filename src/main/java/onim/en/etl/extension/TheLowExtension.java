package onim.en.etl.extension;

import onim.en.etl.ui.parts.ToggleButton;

public abstract class TheLowExtension {

  private boolean enabled = true;

  public abstract String id();
  
  public abstract String category();

  public boolean isEnabled() {
    return this.enabled;
  }

  public void setEnable(boolean b) {
    this.enabled = b;
  }

  public abstract void onEnable();

  public abstract void onDisable();

  public ToggleButton getToggleEnableButton() {
    ToggleButton button = new ToggleButton(this.id(), this.enabled);
    button.setOnAction(() -> {
      this.enabled = button.getState();
      if (this.enabled) {
        ExtensionManager.enableExtension(this);
      } else {
        ExtensionManager.disableExtension(this);
      }
    });
    return button;
  }
}
