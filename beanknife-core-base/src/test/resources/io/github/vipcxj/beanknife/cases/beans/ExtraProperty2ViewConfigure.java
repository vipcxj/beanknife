package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.NewViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = SimpleBean.class, genName = "ExtraProperties2BeanView")
public class ExtraProperty2ViewConfigure extends ExtraProperty1ViewConfigure {

    @NewViewProperty("y")
    private String y;

    @NewViewProperty("z")
    private Class<? extends String> z;
}
