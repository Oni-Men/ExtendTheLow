package onim.en.etl.core.injector;

import org.objectweb.asm.tree.InsnList;

import onim.en.etl.core.HookInjector;
import onim.en.etl.core.ObfuscateType;

public class PatchScoreNullError extends HookInjector {

  public PatchScoreNullError() {
    super("net.minecraft");
    // TODO
  }

  @Override
  public boolean injectHook(InsnList list, ObfuscateType type) {
    // TODO Auto-generated method stub
    return false;
  }


}
