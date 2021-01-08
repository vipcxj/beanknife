package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewPropertiesExcludes.class)
@Inherited
public @interface ViewPropertiesExclude {
    String[] value();
}
