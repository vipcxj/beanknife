package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = MetaAndViewOfOnDiffBean1.class, configClass = MetaAndViewOfOnDiffBean1ViewConfig.class)
public class MetaAndViewOfOnDiffBean1View {

    private int pb;

    public MetaAndViewOfOnDiffBean1View() { }

    public MetaAndViewOfOnDiffBean1View(
        int pb
    ) {
        this.pb = pb;
    }

    public MetaAndViewOfOnDiffBean1View(MetaAndViewOfOnDiffBean1View source) {
        this.pb = source.pb;
    }

    public MetaAndViewOfOnDiffBean1View(MetaAndViewOfOnDiffBean1 source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.MetaAndViewOfOnDiffBean1View should not be null.");
        }
        this.pb = source.getPb();
    }

    public static MetaAndViewOfOnDiffBean1View read(MetaAndViewOfOnDiffBean1 source) {
        if (source == null) {
            return null;
        }
        return new MetaAndViewOfOnDiffBean1View(source);
    }

    public static MetaAndViewOfOnDiffBean1View[] read(MetaAndViewOfOnDiffBean1[] sources) {
        if (sources == null) {
            return null;
        }
        MetaAndViewOfOnDiffBean1View[] results = new MetaAndViewOfOnDiffBean1View[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<MetaAndViewOfOnDiffBean1View> read(List<MetaAndViewOfOnDiffBean1> sources) {
        if (sources == null) {
            return null;
        }
        List<MetaAndViewOfOnDiffBean1View> results = new ArrayList<>();
        for (MetaAndViewOfOnDiffBean1 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<MetaAndViewOfOnDiffBean1View> read(Set<MetaAndViewOfOnDiffBean1> sources) {
        if (sources == null) {
            return null;
        }
        Set<MetaAndViewOfOnDiffBean1View> results = new HashSet<>();
        for (MetaAndViewOfOnDiffBean1 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<MetaAndViewOfOnDiffBean1View> read(Stack<MetaAndViewOfOnDiffBean1> sources) {
        if (sources == null) {
            return null;
        }
        Stack<MetaAndViewOfOnDiffBean1View> results = new Stack<>();
        for (MetaAndViewOfOnDiffBean1 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, MetaAndViewOfOnDiffBean1View> read(Map<K, MetaAndViewOfOnDiffBean1> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, MetaAndViewOfOnDiffBean1View> results = new HashMap<>();
        for (Map.Entry<K, MetaAndViewOfOnDiffBean1> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public int getPb() {
        return this.pb;
    }

}
