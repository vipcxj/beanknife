package io.github.vipcxj.beanknife.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(RemoveViewProperties.class)
public @interface RemoveViewProperty {
    String value();
}
