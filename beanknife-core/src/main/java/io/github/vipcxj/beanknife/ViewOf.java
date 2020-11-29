package io.github.vipcxj.beanknife;

import io.github.vipcxj.beanknife.utils.Self;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewOfs.class)
public @interface ViewOf {
    Class<?> value() default Self.class;
    String genPackage() default "";
    String genName() default "";
    String[] includes() default {};
    String[] excludes() default {};
    String includePattern() default "";
    String excludePattern() default "";
}
