package onim.en.etl.core.injector;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
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

    ListIterator<AbstractInsnNode> itr = list.iterator();
    AbstractInsnNode next;
    while (itr.hasNext()) {
      next = itr.next();

      if (next.getOpcode() != Opcodes.FRETURN)
        continue;

      InsnList injectings = new InsnList();
      injectings.add(new VarInsnNode(Opcodes.ILOAD, 1));
      injectings.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "onim/en/etl/Hooks",
          "onGetCharWidth", "(FC)F", false));
      list.insertBefore(next, injectings);
    }

    return true;
  }

}
