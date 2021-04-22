package onim.en.etl.ui;

import java.util.Stack;

import onim.en.etl.ui.parts.ComponentBase;

public class RenderingContext implements Cloneable {

  private static final Stack<RenderingContext> stack = new Stack<>();

  public static RenderingContext current;

  public static void push() {
    if (current == null) {
      current = new RenderingContext(0, 0);
    }
    stack.push(current);
    current = current.clone();
  }

  public static void pop() {
    if (stack.isEmpty()) {
      return;
    }
    current = stack.pop();
  }

  public static void translate(int x, int y) {
    if (current == null) {
      current = new RenderingContext(0, 0);
    }
    current.x += x;
    current.y += y;
  }

  public static void color(int color) {
    if (current == null) {
      current = new RenderingContext(0, 0);
    }
    current.color = color;
  }

  public static void clearStack() {
    stack.clear();
  }

  public int x;

  public int y;

  public final int mouseX;

  public final int mouseY;

  public float alpha;

  public int color = 0xFFFFFF;

  public RenderingContext(int mouseX, int mouseY) {
    this.mouseX = mouseX;
    this.mouseY = mouseY;
  }

  @Override
  protected RenderingContext clone() {
    RenderingContext ctx = new RenderingContext(this.mouseX, this.mouseY);
    ctx.x = this.x;
    ctx.y = this.y;
    ctx.alpha = this.alpha;
    return ctx;
  }

  public static boolean isHovering(ComponentBase base) {
    if (current == null) {
      return false;
    }
    boolean xInside = current.mouseX >= current.x && current.mouseX < current.x + base.width;
    boolean yInside = current.mouseY >= current.y && current.mouseY < current.y + base.height;
    return xInside && yInside;
  }
}
