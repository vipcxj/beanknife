package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = NestedGenericBean.class, configClass = NestedGenericBean.class)
public class NestedGenericBeanView<T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> {

    private T1 a;

    private T2 b;

    public NestedGenericBeanView() { }

    public NestedGenericBeanView(
        T1 a,
        T2 b
    ) {
        this.a = a;
        this.b = b;
    }

    public NestedGenericBeanView(NestedGenericBeanView<T1, T2> source) {
        this.a = source.a;
        this.b = source.b;
    }

    public NestedGenericBeanView(NestedGenericBean<T1, T2> source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.NestedGenericBeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
    }

    public static <T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> NestedGenericBeanView<T1, T2> read(NestedGenericBean<T1, T2> source) {
        if (source == null) {
            return null;
        }
        return new NestedGenericBeanView<>(source);
    }

    public static <T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> NestedGenericBeanView<T1, T2>[] read(NestedGenericBean<T1, T2>[] sources) {
        if (sources == null) {
            return null;
        }
        NestedGenericBeanView<T1, T2>[] results = new NestedGenericBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static <T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> List<NestedGenericBeanView<T1, T2>> read(List<NestedGenericBean<T1, T2>> sources) {
        if (sources == null) {
            return null;
        }
        List<NestedGenericBeanView<T1, T2>> results = new ArrayList<>();
        for (NestedGenericBean<T1, T2> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> Set<NestedGenericBeanView<T1, T2>> read(Set<NestedGenericBean<T1, T2>> sources) {
        if (sources == null) {
            return null;
        }
        Set<NestedGenericBeanView<T1, T2>> results = new HashSet<>();
        for (NestedGenericBean<T1, T2> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> Stack<NestedGenericBeanView<T1, T2>> read(Stack<NestedGenericBean<T1, T2>> sources) {
        if (sources == null) {
            return null;
        }
        Stack<NestedGenericBeanView<T1, T2>> results = new Stack<>();
        for (NestedGenericBean<T1, T2> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K, T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> Map<K, NestedGenericBeanView<T1, T2>> read(Map<K, NestedGenericBean<T1, T2>> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, NestedGenericBeanView<T1, T2>> results = new HashMap<>();
        for (Map.Entry<K, NestedGenericBean<T1, T2>> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public T1 getA() {
        return this.a;
    }

    public T2 getB() {
        return this.b;
    }

}
