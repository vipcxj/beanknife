package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = WriteableBean.class, configClass = WriteableBeanViewConfigure.class)
public class WriteableBeanView<T> {

    private String a;

    private boolean b;

    private List<?> c;

    private List<SimpleBean> d;

    public WriteableBeanView() { }

    public WriteableBeanView(
        String a,
        boolean b,
        List<?> c,
        List<SimpleBean> d
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public WriteableBeanView(WriteableBeanView<T> source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
        this.d = source.d;
    }

    public WriteableBeanView(WriteableBean<T> source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.WriteableBeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.isB();
        this.c = source.getC();
        this.d = source.getD();
    }

    public static <T> WriteableBeanView<T> read(WriteableBean<T> source) {
        if (source == null) {
            return null;
        }
        return new WriteableBeanView<>(source);
    }

    public static <T> WriteableBeanView<T>[] read(WriteableBean<T>[] sources) {
        if (sources == null) {
            return null;
        }
        WriteableBeanView<T>[] results = new WriteableBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static <T> List<WriteableBeanView<T>> read(List<WriteableBean<T>> sources) {
        if (sources == null) {
            return null;
        }
        List<WriteableBeanView<T>> results = new ArrayList<>();
        for (WriteableBean<T> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <T> Set<WriteableBeanView<T>> read(Set<WriteableBean<T>> sources) {
        if (sources == null) {
            return null;
        }
        Set<WriteableBeanView<T>> results = new HashSet<>();
        for (WriteableBean<T> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <T> Stack<WriteableBeanView<T>> read(Stack<WriteableBean<T>> sources) {
        if (sources == null) {
            return null;
        }
        Stack<WriteableBeanView<T>> results = new Stack<>();
        for (WriteableBean<T> source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K, T> Map<K, WriteableBeanView<T>> read(Map<K, WriteableBean<T>> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, WriteableBeanView<T>> results = new HashMap<>();
        for (Map.Entry<K, WriteableBean<T>> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public void writeBack(WriteableBean<T> target) {
        target.setA(this.getA());
        target.setB(this.isB());
        target.d = this.getD();
    }

    public String getA() {
        return this.a;
    }

    public boolean isB() {
        return this.b;
    }

    public List<?> getC() {
        return this.c;
    }

    public List<SimpleBean> getD() {
        return this.d;
    }

}
