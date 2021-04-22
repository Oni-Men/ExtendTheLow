package onim.en.etl.qucikaction;

public abstract class CustomAction {

  public final String id;

  public CustomAction(String id) {
    this.id = id;
  }

  public abstract void execute();
}
