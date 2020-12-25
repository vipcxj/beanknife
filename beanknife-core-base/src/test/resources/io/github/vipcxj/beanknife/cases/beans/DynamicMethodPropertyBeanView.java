package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;


@GeneratedView(targetClass = SimpleBean.class, configClass = DynamicMethodPropertyBeanViewConfig.class)
public class DynamicMethodPropertyBeanView {

    private String a;

    public DynamicMethodPropertyBeanView() { }

    public DynamicMethodPropertyBeanView(
        String a
    ) {
        this.a = a;
    }

    public DynamicMethodPropertyBeanView(DynamicMethodPropertyBeanView source) {
        this.a = source.a;
    }

    public static DynamicMethodPropertyBeanView read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        DynamicMethodPropertyBeanView out = new DynamicMethodPropertyBeanView();
        out.a = source.getA();
        return out;
    }

    public static DynamicMethodPropertyBeanView[] read(SimpleBean[] sources) {
        if (sources == null) {
            return null;
        }
        DynamicMethodPropertyBeanView[] results = new DynamicMethodPropertyBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<DynamicMethodPropertyBeanView> read(List<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        List<DynamicMethodPropertyBeanView> results = new ArrayList<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<DynamicMethodPropertyBeanView> read(Set<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<DynamicMethodPropertyBeanView> results = new HashSet<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<DynamicMethodPropertyBeanView> read(Stack<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<DynamicMethodPropertyBeanView> results = new Stack<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, DynamicMethodPropertyBeanView> read(Map<K, SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, DynamicMethodPropertyBeanView> results = new HashMap<>();
        for (Map.Entry<K, SimpleBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public String getA() {
        return this.a;
    }

    public String getB() {
        return DynamicMethodPropertyBeanViewConfig.getB(this);
    }

    public String getC() {
        return DynamicMethodPropertyBeanViewConfig.getC(this);
    }

    public String getD() {
        return DynamicMethodPropertyBeanViewConfig.getABC(this.a, this.getB(), this.getC());
    }

}
