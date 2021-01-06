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
public class SimpleBeanWithProtectedSetters {

        private String a;

        private Integer b;

        private long c;

    public SimpleBeanWithProtectedSetters() { }

    public SimpleBeanWithProtectedSetters(
        String a,
        Integer b,
        long c
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public SimpleBeanWithProtectedSetters(SimpleBeanWithProtectedSetters source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
    }

    public SimpleBeanWithProtectedSetters(SimpleBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.SimpleBeanWithProtectedSetters should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
    }

    public static SimpleBeanWithProtectedSetters read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        return new SimpleBeanWithProtectedSetters(source);
    }

    public static SimpleBeanWithProtectedSetters[] read(SimpleBean[] sources) {
        if (sources == null) {
            return null;
        }
        SimpleBeanWithProtectedSetters[] results = new SimpleBeanWithProtectedSetters[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<SimpleBeanWithProtectedSetters> read(List<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        List<SimpleBeanWithProtectedSetters> results = new ArrayList<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<SimpleBeanWithProtectedSetters> read(Set<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<SimpleBeanWithProtectedSetters> results = new HashSet<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<SimpleBeanWithProtectedSetters> read(Stack<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<SimpleBeanWithProtectedSetters> results = new Stack<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, SimpleBeanWithProtectedSetters> read(Map<K, SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, SimpleBeanWithProtectedSetters> results = new HashMap<>();
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

        protected void setA(String a) {
        this.a = a;
    }

        protected void setB(Integer b) {
        this.b = b;
    }

        protected void setC(long c) {
        this.c = c;
    }

}
