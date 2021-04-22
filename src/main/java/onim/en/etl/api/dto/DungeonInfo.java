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
      int i = this.getLevel();
      if (i == 9999) {
        this.color = new Color(198, 0, 211);
      } else {
        int r = 255 & (int) (55 + (i / 80F) * 200);
        int g = 255 & (int) (55 + (1F - i / 80F) * 200);
        int b = 255 & (int) (55 + (i < 40 ? (0.5 - i / 80F) : (0.5 + i / 80F)) * 200);

        this.color = new Color(r, g, b);
      }
    }
    return this.color;
  }

}
