package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.*;

@ViewOf(WriteableBean.class)
@ViewWriteBackMethod(Access.PUBLIC)
@ViewCreateAndWriteBackMethod(Access.PROTECTED)
@ViewWriteBackExclude(WriteableBeanMeta.f)
public class WriteableBeanViewConfigure extends IncludeAllBaseConfigure {
}
