package onim.en.etl.core.injector;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;

import onim.en.etl.core.HookInjector;
import onim.en.etl.core.ObfuscateType;
import onim.en.etl.util.BytecodeUtil;

public class DrawStringDropShadow extends HookInjector {

  public DrawStringDropShadow() {
    super("net.minecraft.client.gui.FontRenderer");
    this.registerEntry(ObfuscateType.NONE, "drawString", "(Ljava/lang/String;FFIZ)I");
  }

  @Override
  public boolean injectHook(InsnList list, ObfuscateType type) {
    MethodInsnNode hook = new MethodInsnNode(Opcodes.INVOKESTATIC, "onim/en/etl/Hooks", "getShadowOffset", "(F)F", false);
    BytecodeUtil.injectAfterSequence(list, new int[] {Opcodes.FCONST_1}, (loc) -> {
      list.insert(loc, hook);
      return true;
    });

    return false;
  }

}
