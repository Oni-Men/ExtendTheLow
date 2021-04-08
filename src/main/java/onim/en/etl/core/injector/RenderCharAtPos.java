package onim.en.etl.core.injector;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import onim.en.etl.core.HookInjector;
import onim.en.etl.core.ObfuscateType;

public class RenderCharAtPos extends HookInjector {

  public RenderCharAtPos() {
    super("net.minecraft.client.gui.FontRenderer");

    this.registerEntry(ObfuscateType.NONE, "func_181559_a", "(CZ)F");
    this.registerEntry(ObfuscateType.OBF, "a", "(CZ)F");
  }

  @Override
  public boolean injectHook(InsnList list, ObfuscateType type) {

    MethodInsnNode hook = new MethodInsnNode(Opcodes.INVOKESTATIC, "onim/en/etl/Hooks",
        "onRenderCharAtPos", "(Z)V", false);
    
    InsnList inject = new InsnList();

    inject.add(this.stackField(type == ObfuscateType.NONE ? "boldStyle" : "t", "Z"));
    inject.add(hook);

    list.insert(inject);
    
    return true;
  }

  private InsnList stackField(String fieldName, String desc) {
    InsnList list = new InsnList();

    list.add(new VarInsnNode(Opcodes.ALOAD, 0));
    list.add(new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/gui/FontRenderer", fieldName,
        desc));
    
    return list;

  }
}
