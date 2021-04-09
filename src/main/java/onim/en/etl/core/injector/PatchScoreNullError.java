package onim.en.etl.core.injector;

import java.util.ListIterator;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import onim.en.etl.core.HookInjector;
import onim.en.etl.core.ObfuscateType;

public class PatchScoreNullError extends HookInjector {

  private static final String NET_HANDLER_PLAY_CLIENT =
      "net/minecraft/network/play/server/S3EPacketTeams";

  private static final String SCOREBOARD = "net/minecraft/scoreboard/Scoreboard";

  public PatchScoreNullError() {
    super("net.minecraft.client.network.NetHandlerPlayClient");
    // TODO
    super.registerEntry(ObfuscateType.NONE, "handleTeams", "(L" + NET_HANDLER_PLAY_CLIENT + ";)V");
  }

  @Override
  public boolean injectHook(InsnList list, ObfuscateType type) {

    String desc = String.format("(L%s;L%s;)Z", NET_HANDLER_PLAY_CLIENT, SCOREBOARD);
    MethodInsnNode hook =
        new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK, "onHandleTeams", desc, false);

    LabelNode label = new LabelNode();
    InsnList inject = new InsnList();

    inject.add(new VarInsnNode(Opcodes.ALOAD, 1));
    inject.add(new VarInsnNode(Opcodes.ALOAD, 2));
    inject.add(hook);
    inject.add(new JumpInsnNode(Opcodes.IFEQ, label));
    inject.add(new InsnNode(Opcodes.RETURN));
    inject.add(label);

    ListIterator<AbstractInsnNode> itr = list.iterator();

    AbstractInsnNode next;
    while (itr.hasNext()) {
      next = itr.next();

      if (!(next instanceof VarInsnNode))
        continue;

      VarInsnNode varInsn = (VarInsnNode) next;

      if (varInsn.getOpcode() != Opcodes.ASTORE)
        continue;

      if (varInsn.var != 2)
        continue;

      list.insert(varInsn, inject);
      return true;
    }
    
    return false;
  }


}
