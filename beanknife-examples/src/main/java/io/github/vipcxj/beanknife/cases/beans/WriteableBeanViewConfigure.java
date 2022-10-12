package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.*;
import io.github.vipcxj.beanknife.runtime.converters.NullStringAsEmpty;

@ViewOf(WriteableBean.class)
@ViewWriteBackMethod(Access.PUBLIC)
@ViewCreateAndWriteBackMethod(Access.PROTECTED)
@ViewWriteBackExclude(WriteableBeanMeta.f)
public class WriteableBeanViewConfigure extends IncludeAllBaseConfigure {

    @MapViewProperty(name = "mappedA", map = "a")
    @NullStringAsEmpty
    public String mappedA;

}
