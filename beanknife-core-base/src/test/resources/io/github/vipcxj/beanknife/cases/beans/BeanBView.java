package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = BeanB.class, configClass = ConfigBeanB.class)
public class BeanBView {

    private String a;

    public BeanBView() { }

    public BeanBView(
        String a
    ) {
        this.a = a;
    }

    public BeanBView(BeanBView source) {
        this.a = source.a;
    }

    public static BeanBView read(BeanB source) {
        if (source == null) {
            return null;
        }
        BeanBView out = new BeanBView();
        out.a = source.getA();
        return out;
    }

    public static BeanBView[] read(BeanB[] sources) {
        if (sources == null) {
            return null;
        }
        BeanBView[] results = new BeanBView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<BeanBView> read(List<BeanB> sources) {
        if (sources == null) {
            return null;
        }
        List<BeanBView> results = new ArrayList<>();
        for (BeanB source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<BeanBView> read(Set<BeanB> sources) {
        if (sources == null) {
            return null;
        }
        Set<BeanBView> results = new HashSet<>();
        for (BeanB source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<BeanBView> read(Stack<BeanB> sources) {
        if (sources == null) {
            return null;
        }
        Stack<BeanBView> results = new Stack<>();
        for (BeanB source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, BeanBView> read(Map<K, BeanB> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, BeanBView> results = new HashMap<>();
        for (Map.Entry<K, BeanB> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public String getA() {
        return this.a;
    }

}
