package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.MethodAnnotation1;
import io.github.vipcxj.beanknife.cases.annotations.PropertyAnnotation1;
import io.github.vipcxj.beanknife.runtime.annotations.*;

@ViewOf(LombokBean.class)
@ViewOf(LombokBean2.class)
@ViewOf(LombokBean3.class)
@ViewWriteBackMethod(Access.PUBLIC)
@UseAnnotation(PropertyAnnotation1.class)
@UseAnnotation(MethodAnnotation1.class)
public class LombokBeanViewConfigure extends IncludeAllBaseConfigure {
}
