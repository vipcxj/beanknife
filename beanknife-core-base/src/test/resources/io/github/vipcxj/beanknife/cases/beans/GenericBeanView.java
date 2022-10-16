package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = GenericBean.class, configClass = GenericBean.class)
public class GenericBeanView<T1 extends Exception & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> {

    private T1 a;

    private T2 b;

    private List<T1> c;

    private T2[] d;

    private List<Map<String, Map<T1, T2>>[]>[] e;

    public GenericBeanView() { }

    public GenericBeanView(
        T1 a,
        T2 b,
        List<T1> c,
        T2[] d,
        List<Map<String, Map<T1, T2>>[]>[] e
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }

    public GenericBeanView(GenericBeanView<T1, T2> source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
        this.d = source.d;
        this.e = source.e;
    }

    public GenericBeanView(GenericBean<T1, T2> source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.GenericBeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
        this.d = source.getD();
        this.e = source.getE();
    }

    public static <T1 extends Exception & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> GenericBeanView<T1, T2> read(GenericBean<T1, T2> source) {
        if (source == null) {
            return null;
        }
        return new GenericBeanView<>(source);
    }

    public static <T1 extends Exception & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> GenericBeanView<T1, T2>[] read(GenericBean<T1, T2>[] sources) {
        if (sources == null) {
            return null;
        }
        GenericBeanView<T1, T2>[] results = new GenericBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static <T1 extends Exception & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> List<GenericBeanView<T1, T2>> read(List<GenericBean<T1, T2>> sources) {
        if (sources == null) {
            return null;
        }
        List<GenericBeanView<T1, T2>> results = new ArrayList<>();
        for (GenericBean<T1, T2> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <T1 extends Exception & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> Set<GenericBeanView<T1, T2>> read(Set<GenericBean<T1, T2>> sources) {
        if (sources == null) {
            return null;
        }
        Set<GenericBeanView<T1, T2>> results = new HashSet<>();
        for (GenericBean<T1, T2> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <T1 extends Exception & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> Stack<GenericBeanView<T1, T2>> read(Stack<GenericBean<T1, T2>> sources) {
        if (sources == null) {
            return null;
        }
        Stack<GenericBeanView<T1, T2>> results = new Stack<>();
        for (GenericBean<T1, T2> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K, T1 extends Exception & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> Map<K, GenericBeanView<T1, T2>> read(Map<K, GenericBean<T1, T2>> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, GenericBeanView<T1, T2>> results = new HashMap<>();
        for (Map.Entry<K, GenericBean<T1, T2>> source : sources.entrySet()) {
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

    public List<T1> getC() {
        return this.c;
    }

    public T2[] getD() {
        return this.d;
    }

    public List<Map<String, Map<T1, T2>>[]>[] getE() {
        return this.e;
    }

}
