package onim.en.etl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import onim.en.etl.api.DataStorage;
import onim.en.etl.api.HandleAPI;
import onim.en.etl.event.RenderCharAtPosEvent;
import onim.en.etl.extension.ExtensionManager;
import onim.en.etl.extension.quickaction.QuickActionManager;
import onim.en.etl.font.FontGenerateData;
import onim.en.etl.font.FontGenerateWorker;
import onim.en.etl.ui.AdvancedFontRenderer;
import onim.en.etl.ui.AdvancedIngameGUI;
import onim.en.etl.util.GuiUtil;
import onim.en.etl.util.TickTaskExecutor;
import onim.en.etl.util.TickTaskExecutor.TickTask;

@Mod(modid = ExtendTheLow.MODID, version = ExtendTheLow.VERSION)
public class ExtendTheLow {
  public static final String MODID = "onim.en.etl";
  public static final String VERSION = "1.0.10";

  private static ExtendTheLow instance = null;
  private static AdvancedIngameGUI ingameGUI = null;

  public static AdvancedFontRenderer AdvancedFont;

  public static Path configPath = null;
  public static KeyBinding keyOpenModPrefs = new KeyBinding("onim.en.etl.openPrefs", Keyboard.KEY_P, "onim.en.etl.keyCategory");
  public static KeyBinding keyQuickAction = new KeyBinding("onim.en.etl.quickAction", Keyboard.KEY_H, "onim.en.etl.keyCategory");

  public static TickTask apiScheduler = null;

  public static ExtendTheLow getInstance() {
    return instance;
  }

  public ExtendTheLow() {
    instance = this;
  }

  public static void executeCommand(String command) {
    if (Minecraft.getMinecraft().thePlayer != null) {
      TickTaskExecutor.addTask(() -> {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(command);
      });
    }
  }

  @EventHandler
  public void preInit(FMLPreInitializationEvent event) {
    configPath = event.getModConfigurationDirectory().toPath().resolve("onim.en.etl");
    try {
      Files.createDirectories(configPath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @EventHandler
  public void init(FMLInitializationEvent event) {
    Minecraft mc = Minecraft.getMinecraft();

    Prefs.load();
    DataStorage.load();
    QuickActionManager.load();

    ExtensionManager.registerAll();
    ExtensionManager.loadModuleSettings();
    MinecraftForge.EVENT_BUS.register(this);

    ClientRegistry.registerKeyBinding(keyOpenModPrefs);
    ClientRegistry.registerKeyBinding(keyQuickAction);

    AdvancedFont = new AdvancedFontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc
      .getTextureManager());
    ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener(AdvancedFont);
  }

  @EventHandler
  public void complete(FMLLoadCompleteEvent event) {
    Minecraft mc = Minecraft.getMinecraft();
    // mc.fontRendererObj = AdvancedFont;
    ingameGUI = new AdvancedIngameGUI(mc);
  }

  @SubscribeEvent
  public void onClientTick(ClientTickEvent event) {
    if (event.phase != Phase.END) {
      return;
    }

    Minecraft mc = Minecraft.getMinecraft();
    mc.ingameGUI = ingameGUI;

    FontGenerateData result;
    while ((result = FontGenerateWorker.getNextResult()) != null) {
      try {
        int glTextureId = GlStateManager.generateTexture();
        TextureUtil.uploadTextureImage(glTextureId, result.image);
        result.data.setGlTextureId(glTextureId);
        result.data.complete();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    TickTaskExecutor.advanceScheduledTasks();
  }

  @SubscribeEvent
  public void onClientChatReceived(ClientChatReceivedEvent event) {
    event.setCanceled(HandleAPI.processChat(event));

    if (event.message.getFormattedText().startsWith(HandleAPI.PLAYER_DATA_MSG)) {
      HandleAPI.startApiUpdateRoutine();
    }
  }

  @SubscribeEvent
  public void onWorldUnload(WorldEvent.Unload event) {
    if (apiScheduler != null) {
      apiScheduler.cancel();
      apiScheduler = null;
    }
  }

  // @SubscribeEvent
  // public void onGetCharWidth(GetCharWidthEvent event) {
  // if (Prefs.get().betterFont) {
  // event.setWidth(AdvancedFont.getCharWidth(event.getChar()));
  // }
  // }

  @SubscribeEvent
  public void onRenderCharAtPos(RenderCharAtPosEvent event) {
    if (AdvancedFont != null) {
      AdvancedFont.lastRenderCharAtPosEvent = event;
    }
  }

  @SubscribeEvent
  public void onInput(KeyInputEvent event) {
    Minecraft mc = Minecraft.getMinecraft();

    if (mc.currentScreen == null) {
      if (keyOpenModPrefs.isPressed()) {
        GuiUtil.openSettingGUI();
      }

    }

  }
}
