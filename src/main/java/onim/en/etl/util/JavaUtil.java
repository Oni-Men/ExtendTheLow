package onim.en.etl.util;

import java.lang.reflect.Field;
import java.nio.file.NoSuchFileException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class JavaUtil {

  public static Set<Class<?>> getInterfaces(Class<?> target) {
    Set<Class<?>> interfaces = new HashSet<>();
    do {
      for (Class<?> i : target.getInterfaces()) {
        interfaces.add(i);
      }
    } while ((target = target.getSuperclass()) != null);
    return interfaces;
  }

  public static boolean setFieldValue(Object instance, String fieldName, Object value) {
    try {
      Class<? extends Object> target = instance.getClass();
      Field field = target.getDeclaredField(fieldName);
      field.setAccessible(true);
      field.set(instance, value);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isSubClassOf(Class<?> target, Class<?> sub) {
    while ((sub = sub.getSuperclass()) != null) {
      if (target.equals(sub)) {
        return true;
      }
    }
    return false;
  }

  public static boolean setFieldValueRecursion(Object instance, String fieldName, Object value) {
    try {
      Class<?> target = instance.getClass();
      do {
        Field field = getDeclaredField(target, fieldName);
        if (field != null) {
          field.setAccessible(true);
          field.set(instance, value);
          break;
        }
      } while ((target = target.getSuperclass()) != null);
    } catch (Exception e) {
      return false;
    }
    return true;
  }

  public static Field getDeclaredField(Class<?> target, String fieldName) {
    try {
      return target.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      return null;
    }
  }

  public static Field getDeclaredFieldResursion(Class<?> target, String fieldName) {
    while (target != null) {
      try {
        return target.getDeclaredField(fieldName);
      } catch (NoSuchFieldException e) {
        target = target.getSuperclass();
      }
    }
    return null;
  }

  public static Set<Field> getDeclaredFieldsResursion(Class<?> target) {
    HashSet<Field> result = new HashSet<>();
    do {
      for (Field field : target.getDeclaredFields()) {
        result.add(field);
      }
    } while ((target = target.getSuperclass()) != null);
    return result;
  }

  @SuppressWarnings("unchecked")
  public static <T> Callable<T> wrapField(Field field, T arg) {
    return () -> {
      return (T) field.get(arg);
    };
  }


  /**
   * AのフィールドをBのフィールドで上書きする
   * 
   * @param a
   * @param b
   */
  public static void merge(Object a, Object b) {
    Class<? extends Object> classA = a.getClass();
    Class<? extends Object> classB = b.getClass();

    if (!classA.equals(classB)) {
      return;
    }

    for (Field fieldA : getDeclaredFieldsResursion(classA)) {
      try {
        Field fieldB = getDeclaredFieldResursion(classB, fieldA.getName());

        if (fieldB == null) {
          continue;
        }

        fieldB.setAccessible(true);
        Object value = fieldB.get(b);

        if (value == null) {
          continue;
        }

        setFieldValueRecursion(a, fieldA.getName(), value);
      } catch (Exception e) {
        continue;
      }
    }
  }

  public static int parseInt(String s, int orElse) {
    try {
      return Integer.parseInt(s);
    } catch (NumberFormatException e) {
      return orElse;
    }
  }

  public static String padding(String text, String padding) {
    if (text.length() >= padding.length()) {
      return text;
    }
    int i = padding.length();
    StringBuilder builder = new StringBuilder();
    builder.append(padding);
    builder.append(text);

    return builder.substring(builder.length() - i, builder.length());
  }

  public static boolean lengthCheck(Collection<?> c, int i) {
    return (i >= 0 && i < c.size());
  }

  public static boolean executeIOProcess(ExceptionThrows f) {
    try {
      f.execute();
    } catch (NoSuchFileException e) {
      System.err.println(e.getMessage());
      return false;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public interface ExceptionThrows {

    public void execute() throws Exception;
  }
}
