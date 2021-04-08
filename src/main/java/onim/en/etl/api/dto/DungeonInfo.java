package onim.en.etl.api.dto;

import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector3f;

public class DungeonInfo {

  public String name;

  public String difficulty;

  public int x;

  public int y;

  public int z;

  private Vector3f locationCache;

  private Color color = null;

  private int level = -1;

  public Vector3f getLocation() {
    if (this.locationCache == null) {
      this.locationCache = new Vector3f(x, y, z);
    }
    return this.locationCache;
  }

  public int getLevel() {
    if (level == -1) {
      try {
        level = Integer.parseInt(difficulty);
      } catch (NumberFormatException e) {
        level = 9999;
      }
    }
    return level;
  }

  public Color getColor() {
    if (this.color == null) {
      try {
        // TODO レベルに応じてダンジョンの色変化する機能をもっといい感じにする
        float i = (float) Integer.parseInt(difficulty);
        java.awt.Color c = java.awt.Color.getHSBColor((i / 80F), 1F, 1F);
        this.color = new Color(c.getRed(), c.getGreen(), c.getRed());
      } catch (NumberFormatException e) {
        this.color = new Color(198, 0, 211);
      }
    }
    return this.color;
  }

}
