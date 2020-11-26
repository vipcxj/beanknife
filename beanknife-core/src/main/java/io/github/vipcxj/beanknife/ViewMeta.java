package io.github.vipcxj.beanknife;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewMetas.class)
public @interface ViewMeta {
    String value() default "";
    String packageName() default "";
}
