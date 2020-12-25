package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.*;

@ViewOf(value = SimpleBean.class, genName = "DynamicMethodPropertyBeanView", includes = {SimpleBeanMeta.a, SimpleBeanMeta.b})
public class DynamicMethodPropertyBeanViewConfig {

    @OverrideViewProperty("b")
    @Dynamic
    public static String getB(@InjectSelf DynamicMethodPropertyBeanView self) {
        return self.getA();
    }

    @NewViewProperty("c")
    @Dynamic
    public static String getC(@InjectSelf DynamicMethodPropertyBeanView self) {
        return self.getB();
    }

    @NewViewProperty("d")
    @Dynamic
    public static String getABC(@InjectProperty String a, @InjectProperty String b, @InjectProperty String c) {
        return a + b + c;
    }
}
