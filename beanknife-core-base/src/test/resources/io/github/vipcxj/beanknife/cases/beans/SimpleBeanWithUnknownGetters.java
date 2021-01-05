package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = SimpleBean.class, configClass = GetterAccessViewConfig.class)
public class SimpleBeanWithUnknownGetters {

    private String a;

    private Integer b;

    private long c;

    public SimpleBeanWithUnknownGetters() { }

    public SimpleBeanWithUnknownGetters(
        String a,
        Integer b,
        long c
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public SimpleBeanWithUnknownGetters(SimpleBeanWithUnknownGetters source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
    }

    public SimpleBeanWithUnknownGetters(SimpleBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.SimpleBeanWithUnknownGetters should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
    }

    public static SimpleBeanWithUnknownGetters read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        return new SimpleBeanWithUnknownGetters(source);
    }

    public static SimpleBeanWithUnknownGetters[] read(SimpleBean[] sources) {
        if (sources == null) {
            return null;
        }
        SimpleBeanWithUnknownGetters[] results = new SimpleBeanWithUnknownGetters[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<SimpleBeanWithUnknownGetters> read(List<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        List<SimpleBeanWithUnknownGetters> results = new ArrayList<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<SimpleBeanWithUnknownGetters> read(Set<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<SimpleBeanWithUnknownGetters> results = new HashSet<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<SimpleBeanWithUnknownGetters> read(Stack<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<SimpleBeanWithUnknownGetters> results = new Stack<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, SimpleBeanWithUnknownGetters> read(Map<K, SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, SimpleBeanWithUnknownGetters> results = new HashMap<>();
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

}
