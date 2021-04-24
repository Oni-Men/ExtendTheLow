package onim.en.etl.core;

import java.util.function.Function;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;

public class Bytecodes {

  public static boolean injectAfterSequence(InsnList list, int[] sequence, Function<AbstractInsnNode, Boolean> injector) {
    for (int i = 0; i < list.size(); i++) {
      for (int k = 0; k < sequence.length; k++) {
        if (i + k >= list.size()) {
          break;
        }

        AbstractInsnNode node = list.get(i + k);

        if (node.getOpcode() < 0) {
          continue;
        }

        if (node.getOpcode() != sequence[k]) {
          break;
        }

        if (k + 1 == sequence.length) {
          return injector.apply(node);
        }
      }
    }

    return false;
  }

}
