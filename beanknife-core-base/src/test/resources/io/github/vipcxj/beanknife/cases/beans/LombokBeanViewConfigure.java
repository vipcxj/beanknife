package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.MethodAnnotation1;
import io.github.vipcxj.beanknife.cases.annotations.PropertyAnnotation1;
import io.github.vipcxj.beanknife.runtime.annotations.UseAnnotation;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(LombokBean.class)
@ViewOf(LombokBean3.class)
@UseAnnotation(PropertyAnnotation1.class)
@UseAnnotation(MethodAnnotation1.class)
public class LombokBeanViewConfigure extends IncludeAllBaseConfigure {
}
