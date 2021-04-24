package onim.en.etl.core.injector;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import onim.en.etl.core.Bytecodes;
import onim.en.etl.core.HookInjector;
import onim.en.etl.core.ObfuscateType;

/**
 * FontRender#renderStringAtPosにて太字描画をさせない
 * 
 * @author onimen
 *
 */
public class PreventRenderBold extends HookInjector {

  private static final int[] OPCODE_LIST_VANILLA = new int[] {Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.IFEQ, -1,
      Opcodes.LDC, Opcodes.GOTO, -1, Opcodes.FCONST_1, -1, Opcodes.FSTORE};

  private static final int[] OPCODE_LIST_OPTIFINE = new int[] {Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.IFEQ,
      Opcodes.LDC, Opcodes.GOTO, Opcodes.ALOAD, Opcodes.GETFIELD, Opcodes.FSTORE};

  public PreventRenderBold() {
    super("net.minecraft.client.gui.FontRenderer");
    this.registerEntry(ObfuscateType.NONE, "renderStringAtPos", "(Ljava/lang/String;Z)V");
    this.registerEntry(ObfuscateType.OBF, "a", "(Ljava/lang/String;Z)V");
  }

  @Override
  public boolean injectHook(InsnList list, ObfuscateType type) {
    ListIterator<AbstractInsnNode> itr = list.iterator();
    AbstractInsnNode node;
    FieldInsnNode fieldInsnNode = null;
    while (itr.hasNext()) {
      node = itr.next();

      if (!(node instanceof FieldInsnNode)) {
        continue;
      }

      fieldInsnNode = (FieldInsnNode) node;

      if (fieldInsnNode.getOpcode() != Opcodes.GETFIELD) {
        continue;
      }

      if (!fieldInsnNode.desc.equals("Z")) {
        continue;
      }

      // boldStyle is named "s" in obfuscated code
      if (!fieldInsnNode.name.equals(type == ObfuscateType.NONE ? "boldStyle" : "s")) {
        continue;
      }

      node = itr.next();

      if (!(node instanceof JumpInsnNode)) {
        continue;
      }

      break;
    }

    if (fieldInsnNode != null) {
      LdcInsnNode falseNode = new LdcInsnNode(false);
      list.remove(fieldInsnNode.getPrevious());
      list.set(fieldInsnNode, falseNode);
    }

    MethodInsnNode hook = new MethodInsnNode(Opcodes.INVOKESTATIC, "onim/en/etl/Hooks", "getShadowOffsetMinus", "(F)F", false);
    return (Bytecodes.injectAfterSequence(list, OPCODE_LIST_VANILLA, (location) -> {
      list.insertBefore(location, hook);
      return true;
    }) || Bytecodes.injectAfterSequence(list, OPCODE_LIST_OPTIFINE, (location) -> {
      list.insertBefore(location, hook);
      return true;
    }));

  }
}
