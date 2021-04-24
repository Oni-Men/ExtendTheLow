package onim.en.etl.ui.custom;

import onim.en.etl.ui.GuiExtendTheLow;
import onim.en.etl.ui.components.ListView;

public class QuickActionSetting extends GuiExtendTheLow {

  private ListView actionList;

  public QuickActionSetting() {
    super("onim.en.etl.quickAction");
    this.setInitializer(list -> {
      this.actionList = new ListView(100, 50);
      
      actionList.setOnClick(System.out::println);

      list.add(actionList);
    });
  }

}
