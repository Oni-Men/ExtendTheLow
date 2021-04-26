package onim.en.etl.ui.custom;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector2f;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import onim.en.etl.extension.quickaction.QuickActionManager;
import onim.en.etl.ui.GuiExtendTheLow;
import onim.en.etl.ui.RenderingContext;
import onim.en.etl.ui.components.Button;
import onim.en.etl.ui.components.DropdownMenu;
import onim.en.etl.util.ColorUtil;
import onim.en.etl.util.GuiUtil;

public class QuickActionSetting extends GuiExtendTheLow {

  public QuickActionSetting() {
    super("onim.en.etl.quickAction");
    this.setInitializer(list -> {
      Button button = new Button(100, "Reset");
      button.setOnAction(() -> {
        QuickActionManager.reset();
      });
      list.add(button);
    });
  }

  @Override
  public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    RenderingContext.current = new RenderingContext(mouseX, mouseY);
    RenderingContext.push();
    RenderingContext.translate(0, 220);
    super.drawScreen(mouseX, mouseY, partialTicks);
    RenderingContext.pop();

    GlStateManager.pushMatrix();
    GlStateManager.enableBlend();
    GlStateManager.translate(width / 2F, 110, 0);

    String[] actionIds = QuickActionManager.getActionIds();

    float tileAngle = 360F / actionIds.length;
    for (int i = 0; i < actionIds.length; i++) {
      float rad1 = (float) Math.toRadians((i) * tileAngle);
      float rad2 = (float) Math.toRadians((i + 1) * tileAngle);
      double degree = Math.toDegrees(Math.atan2(-mouseY, mouseX));
      if (degree < 0) {
        degree += 360;
      }

      degree = degree % 360;

      ColorUtil.glColor(0x88336699);

      GlStateManager.disableTexture2D();
      GL11.glBegin(GL11.GL_TRIANGLE_STRIP);
      this.addVertex(rad1, 96);
      this.addVertex(rad1, 24);
      this.addVertex(rad2, 96);
      this.addVertex(rad2, 24);
      GL11.glEnd();
      GlStateManager.enableTexture2D();

      Vector2f vec1 = new Vector2f(MathHelper.cos(rad1), MathHelper.sin(rad1));
      Vector2f vec2 = new Vector2f(MathHelper.cos(rad2), MathHelper.sin(rad2));

      vec1.scale(60);
      vec2.scale(60);

      GlStateManager.pushMatrix();
      GlStateManager.translate((vec2.x + vec1.x) / 2, (vec2.y + vec1.y) / 2, 1);

      String[] lines = I18n.format(actionIds[i]).split("\n");

      RenderingContext.push();
      RenderingContext.color(0xFFFFFFFF);

      for (int k = 0; k < lines.length; k++) {
        GuiUtil.drawCenteredString(lines[k].trim(), 0, k * 10 - lines.length / 2F * 10, true);
      }

      RenderingContext.pop();

      GlStateManager.popMatrix();
    }

    GlStateManager.popMatrix();
  }

  @Override
  protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
    int x = mouseX - width / 2;
    int y = mouseY - height / 2;
    if (Math.hypot(x, y) > 24) {
      String[] actionIds = QuickActionManager.getActionIds();
      float tileAngle = 360F / actionIds.length;
      int i = (int) (Math.toDegrees(Math.atan2(y, x)) / tileAngle);
      
      if (i >= 0 && i < actionIds.length) {
        this.openMenu(mouseX, mouseY, i);
      }
    }
    super.mouseClicked(mouseX, mouseY, mouseButton);
  }

  private void openMenu(int x, int y, int index) {
    DropdownMenu menu = new DropdownMenu(x, y, 100, 150);
    String[] actionIds = QuickActionManager.getActionIds();

    for (int i = 0; i < actionIds.length; i++) {
      menu.add(I18n.format(actionIds[i]));
    }

    menu.setOnClick(s -> {

      this.closeDropdownMenu();
    });

    this.openDropdownMenu(menu);
  }

  private void addVertex(float rad, float radius) {
    float x = radius * MathHelper.cos(rad);
    float y = radius * MathHelper.sin(rad);
    GL11.glVertex3f(x, y, 0F);
  }

  @Override
  public void onGuiClosed() {
    super.onGuiClosed();
    QuickActionManager.save();
  }

}

