package onim.en.etl.extension.photo;

public enum GridType {

    NONE,
    DIAGONAL,
    TRISECTION,
    QUARTER;

  public static GridType byString(String name) {
    GridType[] values = GridType.values();
    try {
      int i = Integer.parseInt(name);
      return values[i % values.length];
    } catch (NumberFormatException e) {
      for (GridType type : values) {

        if (type.name().equalsIgnoreCase(name)) {
          return type;
        }
      }
    }

    return null;
  }

}