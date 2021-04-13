package onim.en.etl.util;

import java.lang.reflect.Field;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import onim.en.etl.Prefs;
import onim.en.etl.annotation.PrefItem;
import onim.en.etl.api.DataStorage;
import onim.en.etl.api.HandleAPI;
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
      buttonList.add(getGeneralSettingButton(gui));
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

  public static Button getGeneralSettingButton(GuiScreen prevScreen) {
    Button button = new Button(100, "onim.en.etl.generalSettings");
    button.setOnAction(() -> openGeneralSettingsGUI(prevScreen));
    return button;
  }

  public static void openGeneralSettingsGUI(GuiScreen prevScreen) {
    GuiExtendTheLow gui = new GuiExtendTheLow("onim.en.etl.generalSettings", prevScreen);
    gui.setInitializer(buttonList -> {
      buttonList.add(new ToggleButton("onim.en.etl.advancedFont", Prefs.get().betterFont, b -> {
        Prefs.get().betterFont = b;
      }));
      buttonList
          .add(new ToggleButton("onim.en.etl.customStatus", Prefs.get().customTheLowStatus, b -> {
            Prefs.get().customTheLowStatus = b;
          }));
      buttonList.add(
          new ToggleButton("onim.en.etl.invertCustomStatus", Prefs.get().invertTheLowStatus, b -> {
            Prefs.get().invertTheLowStatus = b;
          }));
      buttonList.add(getClearCacheButton());
    });

    gui.setOnClose(() -> Prefs.save());

    Minecraft.getMinecraft().displayGuiScreen(gui);
  }

  public static Button getClearCacheButton() {
    Button button = new Button(100, "onim.en.etl.clearCache");
    button.setOnAction(() -> {
      DataStorage.clearCache();
      HandleAPI.requestDatas();
    });
    return button;
  }

  public static void drawGradientRectHorizontal(int left, int top, int right, int bottom, int startColor, int endColor) {
    float alpha1 = (float) (startColor >> 24 & 255) / 255.0F;
    float red1 = (float) (startColor >> 16 & 255) / 255.0F;
    float green1 = (float) (startColor >> 8 & 255) / 255.0F;
    float blue1 = (float) (startColor & 255) / 255.0F;
    float alpha2 = (float) (endColor >> 24 & 255) / 255.0F;
    float red2 = (float) (endColor >> 16 & 255) / 255.0F;
    float green2 = (float) (endColor >> 8 & 255) / 255.0F;
    float blue2 = (float) (endColor & 255) / 255.0F;
    GlStateManager.disableTexture2D();
    GlStateManager.enableBlend();
    GlStateManager.disableAlpha();
    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    GlStateManager.shadeModel(7425);
    Tessellator tessellator = Tessellator.getInstance();
    WorldRenderer worldrenderer = tessellator.getWorldRenderer();
    worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
    worldrenderer.func_181662_b((double) right, (double) top, (double) 0).func_181666_a(red2, green2, blue2,
        alpha2).func_181675_d();
    worldrenderer.func_181662_b((double) left, (double) top, (double) 0).func_181666_a(red1, green1, blue1,
        alpha1).func_181675_d();
    worldrenderer.func_181662_b((double) left, (double) bottom, (double) 0).func_181666_a(red1, green1, blue1,
        alpha1).func_181675_d();
    worldrenderer.func_181662_b((double) right, (double) bottom, (double) 0).func_181666_a(red2, green2, blue2,
        alpha2).func_181675_d();
    tessellator.draw();
    GlStateManager.shadeModel(7424);
    GlStateManager.disableBlend();
    GlStateManager.enableAlpha();
    GlStateManager.enableTexture2D();
  }
}
