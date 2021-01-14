package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.BeanProviders;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import io.github.vipcxj.beanknife.runtime.utils.BeanUsage;
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

    private List<? extends Set<? extends T>> c;

    private List<SimpleBean> d;

    private Integer e;

    private long f;

    public WriteableBeanView() { }

    public WriteableBeanView(
        String a,
        boolean b,
        List<? extends Set<? extends T>> c,
        List<SimpleBean> d,
        Integer e,
        long f
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
    }

    public WriteableBeanView(WriteableBeanView<T> source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
        this.d = source.d;
        this.e = source.e;
        this.f = source.f;
    }

    public WriteableBeanView(WriteableBean<T> source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.WriteableBeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.isB();
        this.c = source.getC();
        this.d = source.getD();
        this.e = source.e;
        this.f = source.getF();
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
        target.setA(this.a);
        target.setB(this.b);
        target.setC(this.c);
        target.setD(this.d);
        target.e = this.e;
    }

    protected WriteableBean<T> createAndWriteBack() {
        WriteableBean<T> target = BeanProviders.INSTANCE.get(WriteableBean.class, BeanUsage.CONVERT_BACK, this, false, false);
        target.setA(this.a);
        target.setB(this.b);
        target.setC(this.c);
        target.setD(this.d);
        target.e = this.e;
        return target;
    }

    public String getA() {
        return this.a;
    }

    public boolean isB() {
        return this.b;
    }

    public List<? extends Set<? extends T>> getC() {
        return this.c;
    }

    public List<SimpleBean> getD() {
        return this.d;
    }

    public Integer getE() {
        return this.e;
    }

    public long getF() {
        return this.f;
    }

}
