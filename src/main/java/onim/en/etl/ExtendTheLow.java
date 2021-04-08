package onim.en.etl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import onim.en.etl.api.HandleAPI;
import onim.en.etl.event.GetCharWidthEvent;
import onim.en.etl.event.RenderCharAtPosEvent;
import onim.en.etl.extension.ExtensionManager;
import onim.en.etl.ui.AdvancedFontRenderer;
import onim.en.etl.ui.AdvancedIngameGUI;
import onim.en.etl.util.GuiUtil;
import onim.en.etl.util.TickTaskExecutor;
import onim.en.etl.util.font.FontGenerateData;
import onim.en.etl.util.font.FontGenerateWorker;

@Mod(modid = ExtendTheLow.MODID, version = ExtendTheLow.VERSION)
public class ExtendTheLow {
  public static final String MODID = "onim.en.etl";
  public static final String VERSION = "1.0";

  private static ExtendTheLow instance = null;
  private static AdvancedIngameGUI ingameGUI = null;
  public static AdvancedFontRenderer RenderFont;

  public static Path configPath = null;
  public static KeyBinding keyOpenModPrefs =
      new KeyBinding("onim.en.etl.openPrefs", Keyboard.KEY_P, "onim.en.etl.keyCategory");

  public static ExtendTheLow getInstance() {
    return instance;
  }

  public ExtendTheLow() {
    instance = this;
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
    ExtensionManager.registerAll();
    ExtensionManager.loadModuleSettings();
    MinecraftForge.EVENT_BUS.register(this);

    ClientRegistry.registerKeyBinding(keyOpenModPrefs);

    RenderFont = new AdvancedFontRenderer(mc.gameSettings,
        new ResourceLocation("textures/font/ascii.png"), mc.getTextureManager());
  }

  @EventHandler
  public void complete(FMLLoadCompleteEvent event) {
    Minecraft mc = Minecraft.getMinecraft();
    mc.fontRendererObj = RenderFont;
    ingameGUI = new AdvancedIngameGUI(mc);
  }

  @SubscribeEvent
  public void onClientTick(ClientTickEvent event) {
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

    Runnable task = TickTaskExecutor.getNextTask();
    if (task != null) {
      task.run();
    }
  }

  @SubscribeEvent
  public void onClientChatReceived(ClientChatReceivedEvent event) {
    event.setCanceled(HandleAPI.process(event));

    if (event.message.getFormattedText().startsWith(HandleAPI.PLAYER_DATA_MSG)) {
      for (String type : HandleAPI.API_TYPES) {
        TickTaskExecutor.addTask(() -> {
          Minecraft.getMinecraft().thePlayer.sendChatMessage(String.format("/thelow_api %s", type));
        });
      }
    }
  }

  @SubscribeEvent
  public void onGetCharWidth(GetCharWidthEvent event) {
    event.setCanceled(true);
    event.setWidth(RenderFont.getCharWidth(event.getChar()));
  }

  @SubscribeEvent
  public void onRenderCharAtPos(RenderCharAtPosEvent event) {
    if (RenderFont != null) {
      RenderFont.lastRenderCharAtPosEvent = event;
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
