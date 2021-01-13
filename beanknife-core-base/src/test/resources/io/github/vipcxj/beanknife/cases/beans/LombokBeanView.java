package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.MethodAnnotation1;
import io.github.vipcxj.beanknife.cases.annotations.PropertyAnnotation1;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import lombok.Data;

@GeneratedView(targetClass = LombokBean.class, configClass = LombokBeanViewConfigure.class)
public class LombokBeanView {

    private String a;

    private int b;

    private List<? extends Class<? extends Data>> c;

    public LombokBeanView() { }

    public LombokBeanView(
        String a,
        int b,
        List<? extends Class<? extends Data>> c
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public LombokBeanView(LombokBeanView source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
    }

    public LombokBeanView(LombokBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.LombokBeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
    }

    public static LombokBeanView read(LombokBean source) {
        if (source == null) {
            return null;
        }
        return new LombokBeanView(source);
    }

    public static LombokBeanView[] read(LombokBean[] sources) {
        if (sources == null) {
            return null;
        }
        LombokBeanView[] results = new LombokBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<LombokBeanView> read(List<LombokBean> sources) {
        if (sources == null) {
            return null;
        }
        List<LombokBeanView> results = new ArrayList<>();
        for (LombokBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<LombokBeanView> read(Set<LombokBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<LombokBeanView> results = new HashSet<>();
        for (LombokBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<LombokBeanView> read(Stack<LombokBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<LombokBeanView> results = new Stack<>();
        for (LombokBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, LombokBeanView> read(Map<K, LombokBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, LombokBeanView> results = new HashMap<>();
        for (Map.Entry<K, LombokBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    @PropertyAnnotation1
    public String getA() {
        return this.a;
    }

    @MethodAnnotation1
    public int getB() {
        return this.b;
    }

    @PropertyAnnotation1
    public List<? extends Class<? extends Data>> getC() {
        return this.c;
    }

}
