package onim.en.etl.core;

import java.util.Set;

import com.google.common.collect.HashMultimap;

import onim.en.etl.core.injector.GetCharWidthFloatHook;
import onim.en.etl.core.injector.PreventRenderBold;
import onim.en.etl.core.injector.RenderCharAtPos;


public class HookInjectorRegistry {

  private static HashMultimap<String, HookInjector> injectors = HashMultimap.create();

  public static void registerInjector(HookInjector injector) {
    injectors.put(injector.target, injector);
  }

  public static Set<HookInjector> getInjectorsFor(String transformedName) {
    return injectors.get(transformedName);
  }

  public static boolean hasInjectorFor(String transformedName) {
    return injectors.containsKey(transformedName);
  }

  static {
    registerInjector(new GetCharWidthFloatHook());
    registerInjector(new PreventRenderBold());
    registerInjector(new RenderCharAtPos());
  }
}
