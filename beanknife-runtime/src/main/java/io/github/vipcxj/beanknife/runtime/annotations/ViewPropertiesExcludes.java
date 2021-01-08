package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface ViewPropertiesExcludes {
    ViewPropertiesExclude[] value();
}
