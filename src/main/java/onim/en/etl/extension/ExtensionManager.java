package onim.en.etl.extension;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.reflect.ClassPath;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraftforge.common.MinecraftForge;
import onim.en.etl.ExtendTheLow;
import onim.en.etl.annotation.PrefItem;
import onim.en.etl.util.JavaUtil;

public class ExtensionManager {

  private static final String PACKAGE_NAME = "onim.en.etl.extension";

  private static HashMap<String, TheLowExtension> idToExtension = Maps.newLinkedHashMap();
  private static LinkedListMultimap<String, TheLowExtension> extensionsByCategory = LinkedListMultimap.create();

  public static void registerAll() {
    try {
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      ClassPath.from(loader)
        .getTopLevelClassesRecursive(PACKAGE_NAME)
        .stream()
        .map(info -> info.load())
        .sorted((a, b) -> a.getSimpleName().compareTo(b.getSimpleName()))
        .forEach(c -> {
          register(c);
        });
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void register(Class<?> target) {
    try {
      Validate.isTrue(JavaUtil.isSubClassOf(TheLowExtension.class, target));
    } catch (Exception e) {
      return;
    }

    try {
      Constructor<?> constructor = target.getConstructor();
      constructor.setAccessible(true);
      TheLowExtension extension = (TheLowExtension) constructor.newInstance();
      register(extension);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void register(TheLowExtension extension) {
    idToExtension.put(extension.id(), extension);
    extensionsByCategory.put(extension.category(), extension);

    if (extension.isEnabled()) {
      ExtensionManager.enableExtension(extension);
    } else {
      ExtensionManager.disableExtension(extension);
    }
  }

  public static TheLowExtension getExtension(String extensionId) {
    return idToExtension.get(extensionId);
  }

  public static Collection<TheLowExtension> getExtensions() {
    return idToExtension.values();
  }

  public static Set<String> getCategories() {
    return extensionsByCategory.keySet();
  }

  public static List<TheLowExtension> getCategoryExtensions(String category) {
    return extensionsByCategory.get(category);
  }

  public static void enableExtension(TheLowExtension extension) {
    if (extension == null) {
      return;
    }

    MinecraftForge.EVENT_BUS.unregister(extension);
    MinecraftForge.EVENT_BUS.register(extension);
    extension.onEnable();
    extension.setEnable(true);
  }

  public static void enableExtension(String extensionId) {
    enableExtension(getExtension(extensionId));
  }

  public static void enableAll() {
    for (TheLowExtension extension : getExtensions()) {
      enableExtension(extension);
    }
  }

  public static void disableExtension(TheLowExtension extension) {
    if (extension == null) {
      return;
    }

    MinecraftForge.EVENT_BUS.unregister(extension);
    extension.onDisable();
    extension.setEnable(false);
  }

  public static void disableExtension(String extensionId) {
    disableExtension(getExtension(extensionId));
  }

  public static void disableAll() {
    for (TheLowExtension extension : getExtensions()) {
      disableExtension(extension);
    }
  }

  public static void saveModuleSettings() {
    Gson gson = new GsonBuilder().setExclusionStrategies(new TheLowExtensionStrategy()).create();

    for (TheLowExtension module : getExtensions()) {
      try {
        Path path = ExtendTheLow.configPath.resolve(module.id() + ".json");
        String json = gson.toJson(module);
        Files.write(path, Arrays.asList(json.split("\n")), StandardCharsets.UTF_8);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void loadModuleSettings() {
    for (Entry<String, TheLowExtension> entry : idToExtension.entrySet()) {
      try {
        String json = Files
          .lines(ExtendTheLow.configPath.resolve(entry.getKey() + ".json"), StandardCharsets.UTF_8)
          .collect(Collectors.joining("\n"));

        TheLowExtension module = new Gson().fromJson(json, entry.getValue().getClass());
        JavaUtil.merge(entry.getValue(), module);

        if (module.isEnabled()) {
          ExtensionManager.enableExtension(module);
        }
      } catch (Exception e) {
        continue;
      }
    }
  }

  /**
   * PrefItemのアノテーションがないフィールドをスキップする
   * 
   * @author onigi
   *
   */
  static class TheLowExtensionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
      return !f.getName().equals("enabled") && f.getAnnotation(PrefItem.class) == null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
      return false;
    }
  }
}
