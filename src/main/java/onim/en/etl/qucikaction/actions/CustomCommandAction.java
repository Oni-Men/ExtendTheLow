package onim.en.etl.qucikaction.actions;

import onim.en.etl.qucikaction.CustomAction;

public class CustomCommandAction extends CustomAction {

  public final String name;

  private Runnable action;

  public CustomCommandAction(String name, Runnable action) {
    super("onim.en.etl.quickAction.customCommand");
    this.name = name;
    this.action = action;
  }

  @Override
  public void execute() {
    this.action.run();
  }


}
