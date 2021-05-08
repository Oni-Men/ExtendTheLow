package onim.en.etl.core.injector;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import onim.en.etl.core.HookInjector;
import onim.en.etl.core.ObfuscateType;

public class HookBroadcastSound extends HookInjector {

  private static final String BLOCKPOS = "net/minecraft/util/BlockPos";

  public HookBroadcastSound() {
    super("net.minecraft.client.renderer.RenderGlobal");
    this.registerEntry(ObfuscateType.NONE, "broadcastSound", String.format("(IL%s;I)V", BLOCKPOS));
    this.registerEntry(ObfuscateType.OBF, "a", "(ILcj;I)V");
  }

  @Override
  public boolean injectHook(InsnList list, ObfuscateType type) {
    InsnList inject = new InsnList();

    FieldInsnNode event_bus = new FieldInsnNode(Opcodes.GETSTATIC, "net/minecraftforge/common/MinecraftForge", "EVENT_BUS", "Lnet/minecraftforge/fml/common/eventhandler/EventBus;");
    MethodInsnNode post = new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraftforge/fml/common/eventhandler/EventBus", "post", "(Lnet/minecraftforge/fml/common/eventhandler/Event;)Z", false);
    LabelNode elseNode = new LabelNode();

    inject.add(event_bus);
    inject.add(new TypeInsnNode(Opcodes.NEW, "onim/en/etl/event/BroadcastSoundEvent"));
    inject.add(new InsnNode(Opcodes.DUP));
    inject.add(new VarInsnNode(Opcodes.ILOAD, 1));
    inject
      .add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "onim/en/etl/event/BroadcastSoundEvent", "<init>", "(I)V", false));
    inject.add(post);
    inject.add(new JumpInsnNode(Opcodes.IFEQ, elseNode));
    inject.add(new InsnNode(Opcodes.RETURN));
    inject.add(elseNode);

    list.insert(inject);
    return true;
  }

}
