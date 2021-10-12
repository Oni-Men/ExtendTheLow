package onim.en.etl.core.injector;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import onim.en.etl.core.HookInjector;
import onim.en.etl.core.ObfuscateType;

public class HookRenderItem extends HookInjector {

  private static final String DESC_ITEMSTACK = "Lnet/minecraft/item/ItemStack;";
  private static final String DESC_ITEMSTACK_OBF = "Lzx;";

  public HookRenderItem() {
    super("net.minecraft.client.renderer.entity.RenderItem");
    this.registerEntry(ObfuscateType.NONE, "renderItemIntoGUI", "(Lnet/minecraft/item/ItemStack;II)V");
    this.registerEntry(ObfuscateType.OBF, "a", "(Lzx;II)V");
  }

  @Override
  public boolean injectHook(InsnList list, ObfuscateType type) {

    InsnList injects = new InsnList();

    String desc;
    if (type == ObfuscateType.NONE) {
      desc = toDescriptor("V", DESC_ITEMSTACK, "I", "I");
    } else {
      desc = toDescriptor("V", DESC_ITEMSTACK_OBF, "I", "I");
    }
    MethodInsnNode hookInsn = new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK, "renderItemIntoGUI", desc, false);

    injects.add(new VarInsnNode(Opcodes.ALOAD, 1));
    injects.add(new VarInsnNode(Opcodes.ILOAD, 2));
    injects.add(new VarInsnNode(Opcodes.ILOAD, 3));
    injects.add(hookInsn);
    
    AbstractInsnNode node = list.getLast();
    while (node != null) {
      
      if (node.getOpcode() == Opcodes.RETURN) {
        list.insertBefore(node, injects);
        break;
      }
      
      node = node.getPrevious();
    }
    
    return true;
  }

}
