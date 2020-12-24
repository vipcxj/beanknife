package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = MetaAndViewOfOnDiffBean2.class, configClass = MetaAndViewOfOnDiffBean2ViewConfig.class)
public class MetaAndViewOfOnDiffBean2View {

    private String pa;

    public MetaAndViewOfOnDiffBean2View() { }

    public MetaAndViewOfOnDiffBean2View(
        String pa
    ) {
        this.pa = pa;
    }

    public MetaAndViewOfOnDiffBean2View(MetaAndViewOfOnDiffBean2View source) {
        this.pa = source.pa;
    }

    public static MetaAndViewOfOnDiffBean2View read(MetaAndViewOfOnDiffBean2 source) {
        if (source == null) {
            return null;
        }
        MetaAndViewOfOnDiffBean2View out = new MetaAndViewOfOnDiffBean2View();
        out.pa = source.getPa();
        return out;
    }

    public static MetaAndViewOfOnDiffBean2View[] read(MetaAndViewOfOnDiffBean2[] sources) {
        if (sources == null) {
            return null;
        }
        MetaAndViewOfOnDiffBean2View[] results = new MetaAndViewOfOnDiffBean2View[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<MetaAndViewOfOnDiffBean2View> read(List<MetaAndViewOfOnDiffBean2> sources) {
        if (sources == null) {
            return null;
        }
        List<MetaAndViewOfOnDiffBean2View> results = new ArrayList<>();
        for (MetaAndViewOfOnDiffBean2 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<MetaAndViewOfOnDiffBean2View> read(Set<MetaAndViewOfOnDiffBean2> sources) {
        if (sources == null) {
            return null;
        }
        Set<MetaAndViewOfOnDiffBean2View> results = new HashSet<>();
        for (MetaAndViewOfOnDiffBean2 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<MetaAndViewOfOnDiffBean2View> read(Stack<MetaAndViewOfOnDiffBean2> sources) {
        if (sources == null) {
            return null;
        }
        Stack<MetaAndViewOfOnDiffBean2View> results = new Stack<>();
        for (MetaAndViewOfOnDiffBean2 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, MetaAndViewOfOnDiffBean2View> read(Map<K, MetaAndViewOfOnDiffBean2> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, MetaAndViewOfOnDiffBean2View> results = new HashMap<>();
        for (Map.Entry<K, MetaAndViewOfOnDiffBean2> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public String getPa() {
        return this.pa;
    }

}
