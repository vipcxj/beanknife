package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = MetaAndViewOfOnDiffBean4.NestedBean.class, configClass = MetaAndViewOfOnDiffBean4.NestedBean.class)
public class MetaAndViewOfOnDiffBean4$NestedBeanView {

    private String pa;

    private int pb;

    public MetaAndViewOfOnDiffBean4$NestedBeanView() { }

    public MetaAndViewOfOnDiffBean4$NestedBeanView(
        String pa,
        int pb
    ) {
        this.pa = pa;
        this.pb = pb;
    }

    public MetaAndViewOfOnDiffBean4$NestedBeanView(MetaAndViewOfOnDiffBean4$NestedBeanView source) {
        this.pa = source.pa;
        this.pb = source.pb;
    }

    public MetaAndViewOfOnDiffBean4$NestedBeanView(MetaAndViewOfOnDiffBean4.NestedBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.MetaAndViewOfOnDiffBean4$NestedBeanView should not be null.");
        }
        this.pa = source.getPa();
        this.pb = source.getPb();
    }

    public static MetaAndViewOfOnDiffBean4$NestedBeanView read(MetaAndViewOfOnDiffBean4.NestedBean source) {
        if (source == null) {
            return null;
        }
        return new MetaAndViewOfOnDiffBean4$NestedBeanView(source);
    }

    public static MetaAndViewOfOnDiffBean4$NestedBeanView[] read(MetaAndViewOfOnDiffBean4.NestedBean[] sources) {
        if (sources == null) {
            return null;
        }
        MetaAndViewOfOnDiffBean4$NestedBeanView[] results = new MetaAndViewOfOnDiffBean4$NestedBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<MetaAndViewOfOnDiffBean4$NestedBeanView> read(List<MetaAndViewOfOnDiffBean4.NestedBean> sources) {
        if (sources == null) {
            return null;
        }
        List<MetaAndViewOfOnDiffBean4$NestedBeanView> results = new ArrayList<>();
        for (MetaAndViewOfOnDiffBean4.NestedBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<MetaAndViewOfOnDiffBean4$NestedBeanView> read(Set<MetaAndViewOfOnDiffBean4.NestedBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<MetaAndViewOfOnDiffBean4$NestedBeanView> results = new HashSet<>();
        for (MetaAndViewOfOnDiffBean4.NestedBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<MetaAndViewOfOnDiffBean4$NestedBeanView> read(Stack<MetaAndViewOfOnDiffBean4.NestedBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<MetaAndViewOfOnDiffBean4$NestedBeanView> results = new Stack<>();
        for (MetaAndViewOfOnDiffBean4.NestedBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, MetaAndViewOfOnDiffBean4$NestedBeanView> read(Map<K, MetaAndViewOfOnDiffBean4.NestedBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, MetaAndViewOfOnDiffBean4$NestedBeanView> results = new HashMap<>();
        for (Map.Entry<K, MetaAndViewOfOnDiffBean4.NestedBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public String getPa() {
        return this.pa;
    }

    public int getPb() {
        return this.pb;
    }

}
