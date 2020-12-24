package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import io.github.vipcxj.beanknife.runtime.converters.NullIntegerAsZeroConverter;
import io.github.vipcxj.beanknife.runtime.converters.NullLongAsZeroConverter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = ConverterBean.class, configClass = ConverterBeanConfig.class)
public class ConverterBeanView {

    private long a;

    private Number b;

    public ConverterBeanView() { }

    public ConverterBeanView(
        long a,
        Number b
    ) {
        this.a = a;
        this.b = b;
    }

    public ConverterBeanView(ConverterBeanView source) {
        this.a = source.a;
        this.b = source.b;
    }

    public static ConverterBeanView read(ConverterBean source) {
        if (source == null) {
            return null;
        }
        ConverterBeanView out = new ConverterBeanView();
        out.a = new NullLongAsZeroConverter().convert(source.getA());
        out.b = new NullIntegerAsZeroConverter().convert(source.getB());
        return out;
    }

    public static ConverterBeanView[] read(ConverterBean[] sources) {
        if (sources == null) {
            return null;
        }
        ConverterBeanView[] results = new ConverterBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<ConverterBeanView> read(List<ConverterBean> sources) {
        if (sources == null) {
            return null;
        }
        List<ConverterBeanView> results = new ArrayList<>();
        for (ConverterBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<ConverterBeanView> read(Set<ConverterBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<ConverterBeanView> results = new HashSet<>();
        for (ConverterBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<ConverterBeanView> read(Stack<ConverterBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<ConverterBeanView> results = new Stack<>();
        for (ConverterBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, ConverterBeanView> read(Map<K, ConverterBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, ConverterBeanView> results = new HashMap<>();
        for (Map.Entry<K, ConverterBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public long getA() {
        return this.a;
    }

    public Number getB() {
        return this.b;
    }

}
