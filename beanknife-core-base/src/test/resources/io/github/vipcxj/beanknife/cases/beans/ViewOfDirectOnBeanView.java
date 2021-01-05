package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = ViewOfDirectOnBean.class, configClass = ViewOfDirectOnBean.class)
public class ViewOfDirectOnBeanView {

    private long a;

    private String b;

    public ViewOfDirectOnBeanView() { }

    public ViewOfDirectOnBeanView(
        long a,
        String b
    ) {
        this.a = a;
        this.b = b;
    }

    public ViewOfDirectOnBeanView(ViewOfDirectOnBeanView source) {
        this.a = source.a;
        this.b = source.b;
    }

    public ViewOfDirectOnBeanView(ViewOfDirectOnBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.ViewOfDirectOnBeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
    }

    public static ViewOfDirectOnBeanView read(ViewOfDirectOnBean source) {
        if (source == null) {
            return null;
        }
        return new ViewOfDirectOnBeanView(source);
    }

    public static ViewOfDirectOnBeanView[] read(ViewOfDirectOnBean[] sources) {
        if (sources == null) {
            return null;
        }
        ViewOfDirectOnBeanView[] results = new ViewOfDirectOnBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<ViewOfDirectOnBeanView> read(List<ViewOfDirectOnBean> sources) {
        if (sources == null) {
            return null;
        }
        List<ViewOfDirectOnBeanView> results = new ArrayList<>();
        for (ViewOfDirectOnBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<ViewOfDirectOnBeanView> read(Set<ViewOfDirectOnBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<ViewOfDirectOnBeanView> results = new HashSet<>();
        for (ViewOfDirectOnBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<ViewOfDirectOnBeanView> read(Stack<ViewOfDirectOnBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<ViewOfDirectOnBeanView> results = new Stack<>();
        for (ViewOfDirectOnBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, ViewOfDirectOnBeanView> read(Map<K, ViewOfDirectOnBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, ViewOfDirectOnBeanView> results = new HashMap<>();
        for (Map.Entry<K, ViewOfDirectOnBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public long getA() {
        return this.a;
    }

    public String getB() {
        return this.b;
    }

}
