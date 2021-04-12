package onim.en.etl.core;

import java.util.Iterator;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import onim.en.etl.core.HookInjector.MethodIdentifier;

public class CoreTransformer implements IClassTransformer {

  @Override
  public byte[] transform(String name, String transformedName, byte[] bytes) {
    if (FMLLaunchHandler.side() != Side.CLIENT) {
      return bytes;
    }

    if (!HookInjectorRegistry.hasInjectorFor(transformedName)) {
      return bytes;
    }

    try {
      ClassNode classNode = new ClassNode();
      ClassReader classReader = new ClassReader(bytes);

      classReader.accept(classNode, ClassReader.SKIP_FRAMES);

      Set<HookInjector> injectors = HookInjectorRegistry.getInjectorsFor(transformedName);

      for (Iterator<MethodNode> method = classNode.methods.iterator(); method.hasNext();) {
        MethodNode methodNode = method.next();

        for (HookInjector injector : injectors) {
          callInjector(methodNode, injector);
        }

      }

      ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
      classNode.accept(classWriter);

      return classWriter.toByteArray();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return bytes;
  }

  private void callInjector(MethodNode methodNode, HookInjector injector) {
    for (ObfuscateType type : ObfuscateType.values()) {
      MethodIdentifier entry = injector.getEntry(type);
      if (entry == null)
        continue;

      if (!methodNode.name.equals(entry.methodName))
        continue;

      if (!methodNode.desc.equals(entry.methodDesc))
        continue;

      boolean ok = injector.injectHook(methodNode.instructions, type);
      FMLLog.info("[ExtendTheLow] Inject HOOK %s#%s: %s", injector.target, methodNode.name,
          ok ? "SUCCESS" : "FAILED");
    }
  }

}
