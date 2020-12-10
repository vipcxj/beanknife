package io.github.vipcxj.beanknife.annotations.internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface GeneratedMeta {
    Class<?> targetClass();
    Class<?> configClass();
    Class<?>[] proxies() default {};
}
