package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewWriteBackExcludes.class)
@Inherited
public @interface ViewWriteBackExclude {
    String[] value();
}
