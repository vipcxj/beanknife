package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = MetaAndViewOfOnDiffBean3.class, configClass = MetaAndViewOfOnDiffBean3.class)
public class MetaAndViewOfOnDiffBean3View {

    private String pa;

    private int pb;

    public MetaAndViewOfOnDiffBean3View() { }

    public MetaAndViewOfOnDiffBean3View(
        String pa,
        int pb
    ) {
        this.pa = pa;
        this.pb = pb;
    }

    public MetaAndViewOfOnDiffBean3View(MetaAndViewOfOnDiffBean3View source) {
        this.pa = source.pa;
        this.pb = source.pb;
    }

    public MetaAndViewOfOnDiffBean3View(MetaAndViewOfOnDiffBean3 source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.MetaAndViewOfOnDiffBean3View should not be null.");
        }
        this.pa = source.getPa();
        this.pb = source.getPb();
    }

    public static MetaAndViewOfOnDiffBean3View read(MetaAndViewOfOnDiffBean3 source) {
        if (source == null) {
            return null;
        }
        return new MetaAndViewOfOnDiffBean3View(source);
    }

    public static MetaAndViewOfOnDiffBean3View[] read(MetaAndViewOfOnDiffBean3[] sources) {
        if (sources == null) {
            return null;
        }
        MetaAndViewOfOnDiffBean3View[] results = new MetaAndViewOfOnDiffBean3View[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<MetaAndViewOfOnDiffBean3View> read(List<MetaAndViewOfOnDiffBean3> sources) {
        if (sources == null) {
            return null;
        }
        List<MetaAndViewOfOnDiffBean3View> results = new ArrayList<>();
        for (MetaAndViewOfOnDiffBean3 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<MetaAndViewOfOnDiffBean3View> read(Set<MetaAndViewOfOnDiffBean3> sources) {
        if (sources == null) {
            return null;
        }
        Set<MetaAndViewOfOnDiffBean3View> results = new HashSet<>();
        for (MetaAndViewOfOnDiffBean3 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<MetaAndViewOfOnDiffBean3View> read(Stack<MetaAndViewOfOnDiffBean3> sources) {
        if (sources == null) {
            return null;
        }
        Stack<MetaAndViewOfOnDiffBean3View> results = new Stack<>();
        for (MetaAndViewOfOnDiffBean3 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, MetaAndViewOfOnDiffBean3View> read(Map<K, MetaAndViewOfOnDiffBean3> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, MetaAndViewOfOnDiffBean3View> results = new HashMap<>();
        for (Map.Entry<K, MetaAndViewOfOnDiffBean3> source : sources.entrySet()) {
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
