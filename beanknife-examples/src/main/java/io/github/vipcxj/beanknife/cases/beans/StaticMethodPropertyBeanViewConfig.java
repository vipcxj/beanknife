package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.*;

import java.math.BigInteger;

@ViewOf(value = SimpleBean.class, genName = "StaticMethodPropertyBeanView")
public class StaticMethodPropertyBeanViewConfig extends IncludeAllBaseConfigure {

    @NewViewProperty("one")
    public static int getOne(){
        return 1;
    }

    @OverrideViewProperty(SimpleBeanMeta.b)
    public static Object getB() {
        return null;
    }

    @NewViewProperty("d")
    public static Integer getD(SimpleBean bean) {
        return bean.getB();
    }

    @NewViewProperty("e")
    public static Integer getE(SimpleBean bean) {
        return bean.getB();
    }

    @MapViewProperty(name = "newA", map = SimpleBeanMeta.a)
    public static String newA(@InjectProperty(SimpleBeanMeta.a) String a) {
        return a != null ? a + "-new" : "new";
    }

    @MapViewProperty(name = "newC", map = SimpleBeanMeta.c)
    public BigInteger newC(@InjectProperty(SimpleBeanMeta.b) Integer b, @InjectProperty(SimpleBeanMeta.c) long c) {
        return BigInteger.valueOf((b != null ? b : 0) + c);
    }

    /**
     * test non static method as a static method property.
     * Though the source can be compiled,
     * this will cause a exception in the runtime. Because {@link ViewOf#useDefaultBeanProvider()} is false here.
     * So no bean provider is used. Then the configure class {@link StaticMethodPropertyBeanViewConfig} can not be initialized.
     * @param source the original instance.
     * @return the property value
     */
    @NewViewProperty("e")
    public String getABC(SimpleBean source) {
        return source.getA() + source.getB() + source.getC();
    }
}
