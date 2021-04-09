package onim.en.etl.core;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.tree.InsnList;

public abstract class HookInjector {

  protected static final String HOOK = "onim/en/etl/Hooks";

  static class MethodIdentifier {
    public final String methodName;
    public final String methodDesc;

    public MethodIdentifier(String methodName, String methodDesc) {
      this.methodName = methodName;
      this.methodDesc = methodDesc;
    }
  }

  public final String target;
  private final Map<ObfuscateType, MethodIdentifier> map;

  public HookInjector(String target) {
    this.map = new HashMap<>();
    this.target = target;
  }

  public void registerEntry(ObfuscateType type, String methodName, String methodDesc) {
    map.put(type, new MethodIdentifier(methodName, methodDesc));
  }

  public MethodIdentifier getEntry(ObfuscateType type) {
    return map.get(type);
  }

  public abstract boolean injectHook(InsnList list, ObfuscateType type);

}
