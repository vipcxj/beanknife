package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = NestedGenericBean.DynamicChildBean.class, configClass = NestedGenericBean.DynamicChildBean.class)
public class NestedGenericBean$DynamicChildBeanView<T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>, T3> {

        private T1 a;

        private T2 b;

        private T3 c;

    public NestedGenericBean$DynamicChildBeanView() { }

    public NestedGenericBean$DynamicChildBeanView(
        T1 a,
        T2 b,
        T3 c
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public NestedGenericBean$DynamicChildBeanView(NestedGenericBean$DynamicChildBeanView<T1, T2, T3> source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
    }

    public NestedGenericBean$DynamicChildBeanView(NestedGenericBean<T1, T2>.DynamicChildBean<T3> source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.NestedGenericBean$DynamicChildBeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
    }

    public static <T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>, T3> NestedGenericBean$DynamicChildBeanView<T1, T2, T3> read(NestedGenericBean<T1, T2>.DynamicChildBean<T3> source) {
        if (source == null) {
            return null;
        }
        return new NestedGenericBean$DynamicChildBeanView<>(source);
    }

    public static <T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>, T3> NestedGenericBean$DynamicChildBeanView<T1, T2, T3>[] read(NestedGenericBean<T1, T2>.DynamicChildBean<T3>[] sources) {
        if (sources == null) {
            return null;
        }
        NestedGenericBean$DynamicChildBeanView<T1, T2, T3>[] results = new NestedGenericBean$DynamicChildBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static <T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>, T3> List<NestedGenericBean$DynamicChildBeanView<T1, T2, T3>> read(List<NestedGenericBean<T1, T2>.DynamicChildBean<T3>> sources) {
        if (sources == null) {
            return null;
        }
        List<NestedGenericBean$DynamicChildBeanView<T1, T2, T3>> results = new ArrayList<>();
        for (NestedGenericBean<T1, T2>.DynamicChildBean<T3> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>, T3> Set<NestedGenericBean$DynamicChildBeanView<T1, T2, T3>> read(Set<NestedGenericBean<T1, T2>.DynamicChildBean<T3>> sources) {
        if (sources == null) {
            return null;
        }
        Set<NestedGenericBean$DynamicChildBeanView<T1, T2, T3>> results = new HashSet<>();
        for (NestedGenericBean<T1, T2>.DynamicChildBean<T3> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>, T3> Stack<NestedGenericBean$DynamicChildBeanView<T1, T2, T3>> read(Stack<NestedGenericBean<T1, T2>.DynamicChildBean<T3>> sources) {
        if (sources == null) {
            return null;
        }
        Stack<NestedGenericBean$DynamicChildBeanView<T1, T2, T3>> results = new Stack<>();
        for (NestedGenericBean<T1, T2>.DynamicChildBean<T3> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K, T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>, T3> Map<K, NestedGenericBean$DynamicChildBeanView<T1, T2, T3>> read(Map<K, NestedGenericBean<T1, T2>.DynamicChildBean<T3>> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, NestedGenericBean$DynamicChildBeanView<T1, T2, T3>> results = new HashMap<>();
        for (Map.Entry<K, NestedGenericBean<T1, T2>.DynamicChildBean<T3>> source : sources.entrySet()) {
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

        public T3 getC() {
        return this.c;
    }

}
