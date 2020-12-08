package io.github.vipcxj.beanknife.annotations;

import io.github.vipcxj.beanknife.utils.Self;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewMetas.class)
public @interface ViewMeta {
    String value() default "";
    String packageName() default "";
    Class<?> of() default Self.class;
}
