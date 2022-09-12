package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewPropertiesExcludePatterns.class)
@Inherited
public @interface ViewPropertiesExcludePattern {
    String value();
    boolean override() default false;
}
