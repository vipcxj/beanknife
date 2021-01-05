package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = NestedGenericBean.StaticChildBean.class, configClass = NestedGenericBean.StaticChildBean.class)
public class NestedGenericBean$StaticChildBeanView<T1 extends String> {

    private T1 a;

    public NestedGenericBean$StaticChildBeanView() { }

    public NestedGenericBean$StaticChildBeanView(
        T1 a
    ) {
        this.a = a;
    }

    public NestedGenericBean$StaticChildBeanView(NestedGenericBean$StaticChildBeanView<T1> source) {
        this.a = source.a;
    }

    public NestedGenericBean$StaticChildBeanView(NestedGenericBean.StaticChildBean<T1> source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.NestedGenericBean$StaticChildBeanView should not be null.");
        }
        this.a = source.getA();
    }

    public static <T1 extends String> NestedGenericBean$StaticChildBeanView<T1> read(NestedGenericBean.StaticChildBean<T1> source) {
        if (source == null) {
            return null;
        }
        return new NestedGenericBean$StaticChildBeanView<>(source);
    }

    public static <T1 extends String> NestedGenericBean$StaticChildBeanView<T1>[] read(NestedGenericBean.StaticChildBean<T1>[] sources) {
        if (sources == null) {
            return null;
        }
        NestedGenericBean$StaticChildBeanView<T1>[] results = new NestedGenericBean$StaticChildBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static <T1 extends String> List<NestedGenericBean$StaticChildBeanView<T1>> read(List<NestedGenericBean.StaticChildBean<T1>> sources) {
        if (sources == null) {
            return null;
        }
        List<NestedGenericBean$StaticChildBeanView<T1>> results = new ArrayList<>();
        for (NestedGenericBean.StaticChildBean<T1> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <T1 extends String> Set<NestedGenericBean$StaticChildBeanView<T1>> read(Set<NestedGenericBean.StaticChildBean<T1>> sources) {
        if (sources == null) {
            return null;
        }
        Set<NestedGenericBean$StaticChildBeanView<T1>> results = new HashSet<>();
        for (NestedGenericBean.StaticChildBean<T1> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <T1 extends String> Stack<NestedGenericBean$StaticChildBeanView<T1>> read(Stack<NestedGenericBean.StaticChildBean<T1>> sources) {
        if (sources == null) {
            return null;
        }
        Stack<NestedGenericBean$StaticChildBeanView<T1>> results = new Stack<>();
        for (NestedGenericBean.StaticChildBean<T1> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K, T1 extends String> Map<K, NestedGenericBean$StaticChildBeanView<T1>> read(Map<K, NestedGenericBean.StaticChildBean<T1>> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, NestedGenericBean$StaticChildBeanView<T1>> results = new HashMap<>();
        for (Map.Entry<K, NestedGenericBean.StaticChildBean<T1>> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public T1 getA() {
        return this.a;
    }

}
