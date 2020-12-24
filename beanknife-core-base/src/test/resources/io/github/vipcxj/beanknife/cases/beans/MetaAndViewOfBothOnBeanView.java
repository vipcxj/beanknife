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

@GeneratedView(targetClass = MetaAndViewOfBothOnBean.class, configClass = MetaAndViewOfBothOnBean.class)
public class MetaAndViewOfBothOnBeanView {

    private int a;

    private String c;

    private Date e;

    public MetaAndViewOfBothOnBeanView() { }

    public MetaAndViewOfBothOnBeanView(
        int a,
        String c,
        Date e
    ) {
        this.a = a;
        this.c = c;
        this.e = e;
    }

    public MetaAndViewOfBothOnBeanView(MetaAndViewOfBothOnBeanView source) {
        this.a = source.a;
        this.c = source.c;
        this.e = source.e;
    }

    public static MetaAndViewOfBothOnBeanView read(MetaAndViewOfBothOnBean source) {
        if (source == null) {
            return null;
        }
        MetaAndViewOfBothOnBeanView out = new MetaAndViewOfBothOnBeanView();
        out.a = source.getA();
        out.c = source.getC();
        out.e = source.getE();
        return out;
    }

    public static MetaAndViewOfBothOnBeanView[] read(MetaAndViewOfBothOnBean[] sources) {
        if (sources == null) {
            return null;
        }
        MetaAndViewOfBothOnBeanView[] results = new MetaAndViewOfBothOnBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<MetaAndViewOfBothOnBeanView> read(List<MetaAndViewOfBothOnBean> sources) {
        if (sources == null) {
            return null;
        }
        List<MetaAndViewOfBothOnBeanView> results = new ArrayList<>();
        for (MetaAndViewOfBothOnBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<MetaAndViewOfBothOnBeanView> read(Set<MetaAndViewOfBothOnBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<MetaAndViewOfBothOnBeanView> results = new HashSet<>();
        for (MetaAndViewOfBothOnBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<MetaAndViewOfBothOnBeanView> read(Stack<MetaAndViewOfBothOnBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<MetaAndViewOfBothOnBeanView> results = new Stack<>();
        for (MetaAndViewOfBothOnBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, MetaAndViewOfBothOnBeanView> read(Map<K, MetaAndViewOfBothOnBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, MetaAndViewOfBothOnBeanView> results = new HashMap<>();
        for (Map.Entry<K, MetaAndViewOfBothOnBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public int getA() {
        return this.a;
    }

    public String getC() {
        return this.c;
    }

    public Date getE() {
        return this.e;
    }

}
