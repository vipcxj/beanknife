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

@GeneratedView(targetClass = SimpleBean.class, configClass = StaticMethodPropertyBeanViewConfig.class)
public class StaticMethodPropertyBeanView {

        private String a;

        private Object b;

        private int one;

        private Integer c;

        private Integer d;

        private String e;

    public StaticMethodPropertyBeanView() { }

    public StaticMethodPropertyBeanView(
        String a,
        Object b,
        int one,
        Integer c,
        Integer d,
        String e
    ) {
        this.a = a;
        this.b = b;
        this.one = one;
        this.c = c;
        this.d = d;
        this.e = e;
    }

    public StaticMethodPropertyBeanView(StaticMethodPropertyBeanView source) {
        this.a = source.a;
        this.b = source.b;
        this.one = source.one;
        this.c = source.c;
        this.d = source.d;
        this.e = source.e;
    }

    public StaticMethodPropertyBeanView(SimpleBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.StaticMethodPropertyBeanView should not be null.");
        }
        StaticMethodPropertyBeanViewConfig configureBean = BeanProviders.INSTANCE.get(StaticMethodPropertyBeanViewConfig.class, BeanUsage.CONFIGURE, source, false, false);
        this.a = source.getA();
        this.b = StaticMethodPropertyBeanViewConfig.getB();
        this.one = StaticMethodPropertyBeanViewConfig.getOne();
        this.c = StaticMethodPropertyBeanViewConfig.getC(source);
        this.d = StaticMethodPropertyBeanViewConfig.getD(source);
        this.e = configureBean.getABC(source);
    }

    public static StaticMethodPropertyBeanView read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        return new StaticMethodPropertyBeanView(source);
    }

    public static StaticMethodPropertyBeanView[] read(SimpleBean[] sources) {
        if (sources == null) {
            return null;
        }
        StaticMethodPropertyBeanView[] results = new StaticMethodPropertyBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<StaticMethodPropertyBeanView> read(List<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        List<StaticMethodPropertyBeanView> results = new ArrayList<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<StaticMethodPropertyBeanView> read(Set<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<StaticMethodPropertyBeanView> results = new HashSet<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<StaticMethodPropertyBeanView> read(Stack<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<StaticMethodPropertyBeanView> results = new Stack<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, StaticMethodPropertyBeanView> read(Map<K, SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, StaticMethodPropertyBeanView> results = new HashMap<>();
        for (Map.Entry<K, SimpleBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

        public String getA() {
        return this.a;
    }

        public Object getB() {
        return this.b;
    }

        public int getOne() {
        return this.one;
    }

        public Integer getC() {
        return this.c;
    }

        public Integer getD() {
        return this.d;
    }

        /**
     *  test non static method as a static method property.
     *  Though the source can be compiled,
     *  this will cause a exception in the runtime. Because {@link ViewOf#useDefaultBeanProvider()} is false here.
     *  So no bean provider is used. Then the configure class {@link StaticMethodPropertyBeanViewConfig} can not be initialized.
     *  @return the property value
     */
    public String getE() {
        return this.e;
    }

}
