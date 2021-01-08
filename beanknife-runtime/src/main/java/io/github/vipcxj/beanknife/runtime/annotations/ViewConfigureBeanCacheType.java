package io.github.vipcxj.beanknife.runtime.annotations;

import io.github.vipcxj.beanknife.runtime.utils.CacheType;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface ViewConfigureBeanCacheType {
    CacheType value();
}
