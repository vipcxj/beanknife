package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.MapViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.NullNumberAsZero;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

import java.util.Date;

@ViewOf(value = SimpleBean.class, genName = "MapPropertiesView")
public class MapPropertiesViewConfigure extends IncludeAllBaseConfigure {
    @MapViewProperty(name = "aMap", map = SimpleBeanMeta.a)
    private String aMap;
    @MapViewProperty(name = "bMapWithConverter", map = SimpleBeanMeta.b)
    @NullNumberAsZero
    private int bMapWithConverter;
    @MapViewProperty(name = "cMapUseMethod", map = SimpleBeanMeta.c)
    public static Date cMapUseMethod(SimpleBean source) {
        return new Date(source.getC());
    }
}
