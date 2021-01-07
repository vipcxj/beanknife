package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = FieldBean.class, configClass = FieldBeanViewConfig.class)
public class FieldBeanView {

    private long b;

    private Date c;

    private Number[] d;

    public FieldBeanView() { }

    public FieldBeanView(
        long b,
        Date c,
        Number[] d
    ) {
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public FieldBeanView(FieldBeanView source) {
        this.b = source.b;
        this.c = source.c;
        this.d = source.d;
    }

    public FieldBeanView(FieldBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.FieldBeanView should not be null.");
        }
        this.b = source.b;
        this.c = source.c;
        this.d = source.d;
    }

    public static FieldBeanView read(FieldBean source) {
        if (source == null) {
            return null;
        }
        return new FieldBeanView(source);
    }

    public static FieldBeanView[] read(FieldBean[] sources) {
        if (sources == null) {
            return null;
        }
        FieldBeanView[] results = new FieldBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<FieldBeanView> read(List<FieldBean> sources) {
        if (sources == null) {
            return null;
        }
        List<FieldBeanView> results = new ArrayList<>();
        for (FieldBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<FieldBeanView> read(Set<FieldBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<FieldBeanView> results = new HashSet<>();
        for (FieldBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<FieldBeanView> read(Stack<FieldBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<FieldBeanView> results = new Stack<>();
        for (FieldBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, FieldBeanView> read(Map<K, FieldBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, FieldBeanView> results = new HashMap<>();
        for (Map.Entry<K, FieldBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public long getB() {
        return this.b;
    }

    public Date getC() {
        return this.c;
    }

    public Number[] getD() {
        return this.d;
    }

}
