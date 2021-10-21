package onim.en.etl.extension.photo;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import onim.en.etl.annotation.PrefItem;
import onim.en.etl.extension.TheLowExtension;

public class PhotoMode extends TheLowExtension {

  @PrefItem(id = "onim.en.etl.photo.gridType", type = GridType.class)
  public GridType gridType = GridType.NONE;

  @PrefItem(id = "onim.en.etl.photo.hideNametag", type = boolean.class)
  public boolean hideNametag = false;
  
  @PrefItem(id = "onim.en.etl.photo.worldTime", type = float.class, min = -0.01F, max = 1F, step = 0.01F)
  public float worldTime = -0.99f;
  
  @Override
  public String id() {
    return "onim.en.etl.photo";
  }

  @Override
  public String category() {
    return "onim.en.etl.categories";
  }

  @Override
  public void onEnable() {}

  @Override
  public void onDisable() {}

  @SubscribeEvent
  public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
    if (event.type != ElementType.CROSSHAIRS) {
      return;
    }
    
    ScaledResolution sr = event.resolution;

    if (gridType == GridType.NONE) {
      return;
    }
    
    event.setCanceled(true);
    
    double width = sr.getScaledWidth_double();
    double height = sr.getScaledHeight_double();
    
    double margin = Math.min(width, height) * 0.05;
    
    width -= margin;
    height -= margin;
    
    
    GlStateManager.pushMatrix();
    GlStateManager.translate(margin / 2, margin / 2, 0);
    GlStateManager.color(1F, 1F, 1F, 1F);
    GlStateManager.disableTexture2D();
    GlStateManager.disableLighting();
    GL11.glLineWidth(1f);
    
    WorldRenderer buf = Tessellator.getInstance().getWorldRenderer();
    buf.func_181668_a(GL11.GL_LINES, DefaultVertexFormats.field_181705_e);
    
    renderFrame(width, height);
    
    switch (gridType) {
      case DIAGONAL:
        renderDiagonal(width, height);
        break;
      case TRISECTION:
        renderDivideLines(width, height, 3);
        break;
      case QUARTER:
        renderDivideLines(width, height, 4);
        break;
      default:
        break;
    }
    
    Tessellator.getInstance().draw();
    
    GlStateManager.popMatrix();
    GlStateManager.enableTexture2D();
  }
  
  @SubscribeEvent
  public void preRenderLivingSpecials(RenderLivingEvent.Specials.Pre<EntityLivingBase> event) {
    if (hideNametag) {
      return;
    }

    event.setCanceled(true);
  }

  @SubscribeEvent
  public void onWorldRender(RenderTickEvent event) {
    if (worldTime >= 0) {
      Minecraft mc = Minecraft.getMinecraft();
      WorldClient world = mc.theWorld;
      
      if (world != null) {
        world.setWorldTime((long) (worldTime * 24000));
      }
    }
  }

  private void renderFrame(double w, double h) {
    WorldRenderer buf = Tessellator.getInstance().getWorldRenderer();
    
    buf.func_181662_b(0, 0, 0).func_181675_d();
    buf.func_181662_b(w, 0, 0).func_181675_d();
    
    buf.func_181662_b(w, 0, 0).func_181675_d();
    buf.func_181662_b(w, h, 0).func_181675_d();

    
    buf.func_181662_b(w, h, 0).func_181675_d();
    buf.func_181662_b(0, h, 0).func_181675_d();
    
    buf.func_181662_b(0, h, 0).func_181675_d();
    buf.func_181662_b(0, 0, 0).func_181675_d();
  }

  private void renderDivideLines(double w, double h, int sect) {
    WorldRenderer buf = Tessellator.getInstance().getWorldRenderer();
    for (int i = 1; i < sect; i++) {
      buf.func_181662_b(w / sect * i, 0, 0).func_181675_d();
      buf.func_181662_b(w / sect * i, h, 0).func_181675_d();

      buf.func_181662_b(0, h / sect * i, 0).func_181675_d();
      buf.func_181662_b(w, h / sect * i, 0).func_181675_d();
    }
  }

  private void renderDiagonal(double w, double h) {
    WorldRenderer buf = Tessellator.getInstance().getWorldRenderer();
    buf.func_181662_b(0, 0, 0).func_181675_d();
    buf.func_181662_b(w, h, 0).func_181675_d();

    buf.func_181662_b(w, 0, 0).func_181675_d();
    buf.func_181662_b(0, h, 0).func_181675_d();
  }

}