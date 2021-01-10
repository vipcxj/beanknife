package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.NewViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

import java.util.Date;
import java.util.List;

@ViewOf(value = SimpleBean.class, genName = "ExtraProperties1BeanView")
public class ExtraProperty1ViewConfigure extends IncludeAllBaseConfigure {
    @NewViewProperty("x")
    private List<Date> x;
}
