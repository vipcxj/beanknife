package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.NewViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.OverrideViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = SimpleBean.class, genName = "StaticMethodPropertyBeanView", includes = {SimpleBeanMeta.a, SimpleBeanMeta.b})
public class StaticMethodPropertyBeanViewConfig {

    @NewViewProperty("one")
    public static int getOne(){
        return 1;
    }

    @OverrideViewProperty(SimpleBeanMeta.b)
    public static Object getB() {
        return null;
    }

    @NewViewProperty("c")
    public static Integer getC(SimpleBean bean) {
        return bean.getB();
    }

    @NewViewProperty("d")
    public static Integer getD(SimpleBean bean) {
        return bean.getB();
    }
}
