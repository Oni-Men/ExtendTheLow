package onim.en.etl.core.injector;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import onim.en.etl.core.HookInjector;
import onim.en.etl.core.ObfuscateType;

/**
 * Optifine always uses getCharWidthFloat and vanilla code will be ignored. so cannot change logic
 * by Java's "extend". therefore we need to hack Optifine's logic
 * 
 * @author onigi
 *
 */
public class GetCharWidthFloatHook extends HookInjector {

  public GetCharWidthFloatHook() {
    super("net.minecraft.client.gui.FontRenderer");
    this.registerEntry(ObfuscateType.NONE, "getCharWidthFloat", "(C)F");
  }

  @Override
  public boolean injectHook(InsnList list, ObfuscateType type) {
    InsnList injectings = new InsnList();

    String descriptor = "(C)Lonim/en/etl/event/GetCharWidthEvent;";
    MethodInsnNode onGetCharWidth = new MethodInsnNode(Opcodes.INVOKESTATIC, "onim/en/etl/Hooks",
        "onGetCharWidth", descriptor, false);

    MethodInsnNode isCanceled = new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
        "onim/en/etl/event/GetCharWidthEvent", "isCanceled", "()Z", false);

    MethodInsnNode getWidthFloat = new MethodInsnNode(Opcodes.INVOKEVIRTUAL,
        "onim/en/etl/event/GetCharWidthEvent", "getWidth", "()F", false);

    LabelNode jumpTo = new LabelNode();

    injectings.add(new VarInsnNode(Opcodes.ILOAD, 1));
    injectings.add(onGetCharWidth);
    injectings.add(new VarInsnNode(Opcodes.ASTORE, 5));
    injectings.add(new VarInsnNode(Opcodes.ALOAD, 5));
    injectings.add(isCanceled);
    injectings.add(new JumpInsnNode(Opcodes.IFEQ, jumpTo));
    injectings.add(new VarInsnNode(Opcodes.ALOAD, 5));
    injectings.add(getWidthFloat);
    injectings.add(new InsnNode(Opcodes.FRETURN));
    injectings.add(jumpTo);

    list.insert(injectings);
    return true;
  }

}
