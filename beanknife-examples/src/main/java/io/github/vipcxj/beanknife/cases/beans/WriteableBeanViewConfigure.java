package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.Access;
import io.github.vipcxj.beanknife.runtime.annotations.ViewCreateAndWriteBackMethod;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;
import io.github.vipcxj.beanknife.runtime.annotations.ViewWriteBackMethod;

@ViewOf(WriteableBean.class)
@ViewWriteBackMethod(Access.PUBLIC)
@ViewCreateAndWriteBackMethod(Access.PROTECTED)
public class WriteableBeanViewConfigure extends IncludeAllBaseConfigure {
}
