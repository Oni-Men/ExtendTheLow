package onim.en.etl.util;

import java.lang.reflect.Field;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import onim.en.etl.Prefs;
import onim.en.etl.annotation.PrefItem;
import onim.en.etl.extension.ExtensionManager;
import onim.en.etl.extension.TheLowExtension;
import onim.en.etl.ui.GuiExtendTheLow;
import onim.en.etl.ui.parts.Button;
import onim.en.etl.ui.parts.EnumSwitchButton;
import onim.en.etl.ui.parts.GuiActionButton;
import onim.en.etl.ui.parts.Slider;
import onim.en.etl.ui.parts.ToggleButton;

public class GuiUtil {

  public static void openSettingGUI() {
    GuiExtendTheLow gui = new GuiExtendTheLow("onim.en.etl.settings");
    gui.setInitializer(buttonList -> {
      buttonList.add(getFontSettingButton(gui));
      ExtensionManager.getCategories().forEach(category -> {
        Button button = new Button(100, category);
        button.setOnAction(() -> openCategorySettingGUI(category, gui));
        buttonList.add(button);
      });
    });

    Minecraft.getMinecraft().displayGuiScreen(gui);
  }

  public static void openCategorySettingGUI(String category, GuiScreen prevScreen) {
    GuiExtendTheLow gui = new GuiExtendTheLow(category, prevScreen);
    gui.setInitializer(buttonList -> {
      ExtensionManager.getCategoryExtensions(category).forEach(extension -> {
        Button button = new Button(100, extension.id());
        button.setOnAction(() -> openExtensionSettingGUI(extension, gui));
        buttonList.add(button);
      });
    });

    Minecraft.getMinecraft().displayGuiScreen(gui);
  }

  public static void openExtensionSettingGUI(TheLowExtension extension, GuiScreen prevScreen) {
    GuiExtendTheLow gui = new GuiExtendTheLow(extension.id(), prevScreen);
    gui.setInitializer(buttonList -> {
      buttonList.add(extension.getToggleEnableButton());
      Set<Field> fields = JavaUtil.getDeclaredFieldsResursion(extension.getClass());
      fields.forEach(field -> {
        PrefItem item = field.getAnnotation(PrefItem.class);
        if (item == null)
          return;

        GuiActionButton button = getSuitableButtonForItem(item, field, extension);

        if (button != null) {
          buttonList.add(button);
        }
      });
    });
    gui.setOnClose(() -> ExtensionManager.saveModuleSettings());
    Minecraft.getMinecraft().displayGuiScreen(gui);
  }

  public static GuiActionButton getSuitableButtonForItem(PrefItem item, Field field,
      TheLowExtension extension) {
    field.setAccessible(true);
    switch (item.type().getName()) {
      case "boolean":
        return getToggleButton(item, field, extension);
      case "float":
        return getSliderButton(item, field, extension, false);
      case "int":
        return getSliderButton(item, field, extension, true);
      default:

        if (JavaUtil.isSubClassOf(Enum.class, item.type())) {
          return getEnumSwitchButton(item, field, extension);
        }
    }
    return new Button(100, item.id());
  }

  public static ToggleButton getToggleButton(PrefItem item, Field field,
      TheLowExtension extension) {
    try {
      ToggleButton toggleButton = new ToggleButton(item.id(), field.getBoolean(extension));
      toggleButton.setOnAction(() -> {
        try {
          field.setBoolean(extension, toggleButton.getState());
        } catch (IllegalArgumentException | IllegalAccessException e) {
          e.printStackTrace();
        }
      });
      return toggleButton;
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static Slider getSliderButton(PrefItem item, Field field, TheLowExtension extension,
      boolean integer) {
    try {
      Slider slider = new Slider(100, item.id(), field.getFloat(extension), item.min(), item.max(),
          item.step(), integer);
      if (integer) {
        slider.formatter = (f) -> String.format("%.0f%s", f, item.unit());
      } else {
        slider.formatter = (f) -> String.format("%.2f%s", f, item.unit());
      }
      slider.setOnAction(() -> {
        try {
          if (integer) {
            field.setInt(extension, slider.getInt());
          } else {
            field.setFloat(extension, slider.getFloat());
          }
        } catch (IllegalArgumentException | IllegalAccessException e) {
          e.printStackTrace();
        }
      });
      return slider;
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static EnumSwitchButton<?> getEnumSwitchButton(PrefItem item, Field field,
      TheLowExtension extension) {
    try {
      Enum value = (Enum) field.get(extension);
      EnumSwitchButton<?> button = new EnumSwitchButton(100, value.name(), item.type());
      button.setOnAction(() -> {
        try {
          field.set(extension, button.getValue());
        } catch (IllegalArgumentException | IllegalAccessException e) {
          e.printStackTrace();
        }
      });
      return button;
    } catch (IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  public static Button getFontSettingButton(GuiScreen prevScreen) {
    Button button = new Button(100, "onim.en.etl.fontSettings");
    button.setOnAction(() -> openFontSettingsGUI(prevScreen));

    return button;
  }

  public static void openFontSettingsGUI(GuiScreen prevScreen) {
    GuiExtendTheLow gui = new GuiExtendTheLow("onim.en.etl.fontSettings", prevScreen);
    gui.setInitializer(buttonList -> {
      buttonList
          .add(new ToggleButton("onim.en.etl.toggleEnabled", Prefs.get().betterFont, b -> {
            Prefs.get().betterFont = b;
          }));
    });

    gui.setOnClose(() -> Prefs.save());
    
    Minecraft.getMinecraft().displayGuiScreen(gui);
  }
}
