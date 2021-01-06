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
public class SimpleBeanWithProtectedGetters {

        private String a;

        private Integer b;

        private long c;

    public SimpleBeanWithProtectedGetters() { }

    public SimpleBeanWithProtectedGetters(
        String a,
        Integer b,
        long c
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public SimpleBeanWithProtectedGetters(SimpleBeanWithProtectedGetters source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
    }

    public SimpleBeanWithProtectedGetters(SimpleBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.SimpleBeanWithProtectedGetters should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
    }

    public static SimpleBeanWithProtectedGetters read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        return new SimpleBeanWithProtectedGetters(source);
    }

    public static SimpleBeanWithProtectedGetters[] read(SimpleBean[] sources) {
        if (sources == null) {
            return null;
        }
        SimpleBeanWithProtectedGetters[] results = new SimpleBeanWithProtectedGetters[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<SimpleBeanWithProtectedGetters> read(List<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        List<SimpleBeanWithProtectedGetters> results = new ArrayList<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<SimpleBeanWithProtectedGetters> read(Set<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<SimpleBeanWithProtectedGetters> results = new HashSet<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<SimpleBeanWithProtectedGetters> read(Stack<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<SimpleBeanWithProtectedGetters> results = new Stack<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, SimpleBeanWithProtectedGetters> read(Map<K, SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, SimpleBeanWithProtectedGetters> results = new HashMap<>();
        for (Map.Entry<K, SimpleBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

        protected String getA() {
        return this.a;
    }

        protected Integer getB() {
        return this.b;
    }

        protected long getC() {
        return this.c;
    }

}
