package onim.en.etl.extension.quickaction;

public abstract class CustomAction {

  public final String id;

  public CustomAction(String id) {
    this.id = id;
  }

  public abstract void execute();
}
