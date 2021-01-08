package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewPropertiesIncludePatterns.class)
@Inherited
public @interface ViewPropertiesIncludePattern {
    String value();
}
