package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewPropertiesIncludes.class)
@Inherited
public @interface ViewPropertiesInclude {
    String[] value();
    boolean override() default false;
}
