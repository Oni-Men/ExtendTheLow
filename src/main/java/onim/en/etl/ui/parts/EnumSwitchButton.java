package onim.en.etl.ui.parts;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class EnumSwitchButton<T extends Enum<T>> extends Button {

  private List<String> textList;
  private int index = 0;
  private Class<T> enumType;

  private String enumValue;

  public EnumSwitchButton(int widthIn, String initialValue, Class<T> enumType) {
    super(widthIn, "");
    this.enumType = enumType;
    this.textList =
        Stream.of(enumType.getEnumConstants()).map(e -> e.name()).collect(Collectors.toList());
    if (initialValue != null) {
      this.enumValue = initialValue;
    } else if (!this.textList.isEmpty()) {
      this.enumValue = textList.get(0);
    }
    this.displayTranslateString();
  }

  @Override
  public void onClick() {
    index++;
    if (index >= textList.size()) {
      index = 0;
    }
    this.enumValue = textList.get(index);
    this.displayTranslateString();
    super.onClick();
  }

  @Override
  public void drawButton(Minecraft mc, int mouseX, int mouseY) {
    super.drawButton(mc, mouseX, mouseY);
  }

  private void displayTranslateString() {
    this.displayString = I18n.format(this.enumType.getName() + "." + this.enumValue);
  }

  public T getValue() {
    return Enum.valueOf(enumType, this.enumValue);
  }
}
