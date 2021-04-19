package onim.en.etl.ui.parts;

import java.util.HashMap;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public abstract class ComponentBase extends Gui {

  public boolean visible = true;
  public boolean enabled = true;
  public boolean hovered = false;

  public int width;
  public int height;

  public int paddingTop = 4;
  public int paddingBottom = 4;

  public static class Registry {
    private static HashMap<Integer, ComponentBase> components = Maps.newHashMap();

    public static int nextComponent(ComponentBase component) {
      int id = components.size();
      components.put(id, component);
      return id;
    }

    public static ComponentBase getButton(int id) {
      return components.get(id);
    }
  }

  public final int id;

  public ComponentBase() {
    this.id = Registry.nextComponent(this);
  }

  public abstract int draw(Minecraft mc);

  public void update() {

  }

  public void mousePressed(int button) {

  }

  public void mouseReleased(int button, boolean isInside) {

  }

  public void mouseDragged() {

  }
  
}

