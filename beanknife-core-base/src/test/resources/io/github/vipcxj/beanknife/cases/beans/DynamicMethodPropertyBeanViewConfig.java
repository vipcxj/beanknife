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

    /**
     * test non static method as a dynamic method property.
     * Though the source can be compiled,
     * this will cause a exception in the runtime. Because {@link ViewOf#useDefaultBeanProvider()} is false here.
     * So no bean provider is used. Then the configure class {@link DynamicMethodPropertyBeanViewConfig} can not be initialized.
     * @param self the view instance.
     * @return the property value
     */
    @NewViewProperty("e")
    @Dynamic
    public String getABCD(@InjectSelf DynamicMethodPropertyBeanView self) {
        return self.getA() + self.getB() + self.getC() + self.getD();
    }
}
