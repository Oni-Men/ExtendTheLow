package onim.en.etl.annotation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(FIELD)
public @interface PrefItem {

  String id();

  String format() default ".*";

  Class<?> type();

  float min() default 0F;

  float max() default 1F;

  float step() default 0.01F;

  String unit() default "";
}
