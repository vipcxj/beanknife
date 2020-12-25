package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;

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

    public StaticMethodPropertyBeanView() { }

    public StaticMethodPropertyBeanView(
        String a,
        Object b,
        int one,
        Integer c,
        Integer d
    ) {
        this.a = a;
        this.b = b;
        this.one = one;
        this.c = c;
        this.d = d;
    }

    public StaticMethodPropertyBeanView(StaticMethodPropertyBeanView source) {
        this.a = source.a;
        this.b = source.b;
        this.one = source.one;
        this.c = source.c;
        this.d = source.d;
    }

    public static StaticMethodPropertyBeanView read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        StaticMethodPropertyBeanView out = new StaticMethodPropertyBeanView();
        out.a = source.getA();
        out.b = StaticMethodPropertyBeanViewConfig.getB();
        out.one = StaticMethodPropertyBeanViewConfig.getOne();
        out.c = StaticMethodPropertyBeanViewConfig.getC(source);
        out.d = StaticMethodPropertyBeanViewConfig.getD(source);
        return out;
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

}
