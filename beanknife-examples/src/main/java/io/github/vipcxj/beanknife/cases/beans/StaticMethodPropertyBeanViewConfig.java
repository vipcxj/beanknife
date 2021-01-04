package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.*;

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

    /**
     * test non static method as a static method property.
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
