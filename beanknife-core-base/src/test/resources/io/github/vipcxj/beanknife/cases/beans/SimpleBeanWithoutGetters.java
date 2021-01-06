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
public class SimpleBeanWithoutGetters {

        private String a;

        private Integer b;

        private long c;

    public SimpleBeanWithoutGetters() { }

    public SimpleBeanWithoutGetters(
        String a,
        Integer b,
        long c
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public SimpleBeanWithoutGetters(SimpleBeanWithoutGetters source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
    }

    public SimpleBeanWithoutGetters(SimpleBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.SimpleBeanWithoutGetters should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
    }

    public static SimpleBeanWithoutGetters read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        return new SimpleBeanWithoutGetters(source);
    }

    public static SimpleBeanWithoutGetters[] read(SimpleBean[] sources) {
        if (sources == null) {
            return null;
        }
        SimpleBeanWithoutGetters[] results = new SimpleBeanWithoutGetters[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<SimpleBeanWithoutGetters> read(List<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        List<SimpleBeanWithoutGetters> results = new ArrayList<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<SimpleBeanWithoutGetters> read(Set<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<SimpleBeanWithoutGetters> results = new HashSet<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<SimpleBeanWithoutGetters> read(Stack<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<SimpleBeanWithoutGetters> results = new Stack<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, SimpleBeanWithoutGetters> read(Map<K, SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, SimpleBeanWithoutGetters> results = new HashMap<>();
        for (Map.Entry<K, SimpleBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

}
