package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.BeanProviders;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import io.github.vipcxj.beanknife.runtime.utils.BeanUsage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = SimpleBean.class, configClass = DynamicMethodPropertyBeanViewConfig.class)
public class DynamicMethodPropertyBeanView {

    private String a;

    private transient DynamicMethodPropertyBeanViewConfig cachedConfigureBean;

    public DynamicMethodPropertyBeanView() { }

    public DynamicMethodPropertyBeanView(
        String a
    ) {
        this.a = a;
    }

    public DynamicMethodPropertyBeanView(DynamicMethodPropertyBeanView source) {
        this.a = source.a;
    }

    public DynamicMethodPropertyBeanView(SimpleBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.DynamicMethodPropertyBeanView should not be null.");
        }
        this.a = source.getA();
    }

    public static DynamicMethodPropertyBeanView read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        return new DynamicMethodPropertyBeanView(source);
    }

    public static DynamicMethodPropertyBeanView[] read(SimpleBean[] sources) {
        if (sources == null) {
            return null;
        }
        DynamicMethodPropertyBeanView[] results = new DynamicMethodPropertyBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<DynamicMethodPropertyBeanView> read(List<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        List<DynamicMethodPropertyBeanView> results = new ArrayList<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<DynamicMethodPropertyBeanView> read(Set<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<DynamicMethodPropertyBeanView> results = new HashSet<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<DynamicMethodPropertyBeanView> read(Stack<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<DynamicMethodPropertyBeanView> results = new Stack<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, DynamicMethodPropertyBeanView> read(Map<K, SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, DynamicMethodPropertyBeanView> results = new HashMap<>();
        for (Map.Entry<K, SimpleBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public String getA() {
        return this.a;
    }

    public String getB() {
        return DynamicMethodPropertyBeanViewConfig.getB(this);
    }

    public String getC() {
        return DynamicMethodPropertyBeanViewConfig.getC(this);
    }

    public String getD() {
        return DynamicMethodPropertyBeanViewConfig.getABC(this.a, this.getB(), this.getC());
    }

    /**
     *  test non static method as a dynamic method property.
     *  Though the source can be compiled,
     *  this will cause a exception in the runtime. Because {@link ViewOf#useDefaultBeanProvider()} is false here.
     *  So no bean provider is used. Then the configure class {@link DynamicMethodPropertyBeanViewConfig} can not be initialized.
     *  @return the property value
     */
    public String getE() {
        return this.gottenCachedConfigureBean().getABCD(this);
    }

    public DynamicMethodPropertyBeanViewConfig gottenCachedConfigureBean() {
        if (cachedConfigureBean == null) {
            this.cachedConfigureBean = BeanProviders.INSTANCE.get(DynamicMethodPropertyBeanViewConfig.class, BeanUsage.CONFIGURE, this, false, false);
        }
        return this.cachedConfigureBean;
    }

}
