package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = SimpleBean.class, configClass = SetterAccessViewConfig.class)
public class SimpleBeanWithPrivateSetters {

    private String a;

    private Integer b;

    private long c;

    public SimpleBeanWithPrivateSetters() { }

    public SimpleBeanWithPrivateSetters(
        String a,
        Integer b,
        long c
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public SimpleBeanWithPrivateSetters(SimpleBeanWithPrivateSetters source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
    }

    public SimpleBeanWithPrivateSetters(SimpleBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.SimpleBeanWithPrivateSetters should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
    }

    public static SimpleBeanWithPrivateSetters read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        return new SimpleBeanWithPrivateSetters(source);
    }

    public static SimpleBeanWithPrivateSetters[] read(SimpleBean[] sources) {
        if (sources == null) {
            return null;
        }
        SimpleBeanWithPrivateSetters[] results = new SimpleBeanWithPrivateSetters[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<SimpleBeanWithPrivateSetters> read(List<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        List<SimpleBeanWithPrivateSetters> results = new ArrayList<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<SimpleBeanWithPrivateSetters> read(Set<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<SimpleBeanWithPrivateSetters> results = new HashSet<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<SimpleBeanWithPrivateSetters> read(Stack<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<SimpleBeanWithPrivateSetters> results = new Stack<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, SimpleBeanWithPrivateSetters> read(Map<K, SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, SimpleBeanWithPrivateSetters> results = new HashMap<>();
        for (Map.Entry<K, SimpleBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public String getA() {
        return this.a;
    }

    public Integer getB() {
        return this.b;
    }

    public long getC() {
        return this.c;
    }

    private void setA(String a) {
        this.a = a;
    }

    private void setB(Integer b) {
        this.b = b;
    }

    private void setC(long c) {
        this.c = c;
    }

}
