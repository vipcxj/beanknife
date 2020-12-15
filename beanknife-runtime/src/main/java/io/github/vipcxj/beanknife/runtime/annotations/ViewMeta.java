package io.github.vipcxj.beanknife.runtime.annotations;

import io.github.vipcxj.beanknife.runtime.utils.Self;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewMetas.class)
public @interface ViewMeta {
    String value() default "";
    String packageName() default "";
    Class<?> of() default Self.class;
}
