package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RemoveViewProperties.class)
@Inherited
public @interface RemoveViewProperty {
    String value();
}
