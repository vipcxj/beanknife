package io.github.vipcxj.beanknife;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewOfs.class)
public @interface ViewOf {
    Class<?> value() default Self.class;
    String[] includes() default {};
    String[] excludes() default {};
    String includePattern() default "";
    String excludePattern() default "";
}
