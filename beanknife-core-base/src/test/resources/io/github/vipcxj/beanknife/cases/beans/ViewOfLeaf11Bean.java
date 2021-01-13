package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = Leaf11Bean.class, configClass = Leaf11BeanViewConfigure.class)
public class ViewOfLeaf11Bean implements Serializable {
    private static final long serialVersionUID = 0L;

    private Class<? extends Annotation> b;

    private List<? extends String> c;

    private long d;

    private String e;

    public ViewOfLeaf11Bean() { }

    public ViewOfLeaf11Bean(
        Class<? extends Annotation> b,
        List<? extends String> c,
        long d,
        String e
    ) {
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }

    public ViewOfLeaf11Bean(ViewOfLeaf11Bean source) {
        this.b = source.b;
        this.c = source.c;
        this.d = source.d;
        this.e = source.e;
    }

    public static ViewOfLeaf11Bean read(Leaf11Bean source) {
        if (source == null) {
            return null;
        }
        ViewOfLeaf11Bean out = new ViewOfLeaf11Bean();
        out.b = source.getB();
        out.c = source.getC();
        out.d = source.getD();
        out.e = source.getE();
        return out;
    }

    public static ViewOfLeaf11Bean[] read(Leaf11Bean[] sources) {
        if (sources == null) {
            return null;
        }
        ViewOfLeaf11Bean[] results = new ViewOfLeaf11Bean[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<ViewOfLeaf11Bean> read(List<Leaf11Bean> sources) {
        if (sources == null) {
            return null;
        }
        List<ViewOfLeaf11Bean> results = new ArrayList<>();
        for (Leaf11Bean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<ViewOfLeaf11Bean> read(Set<Leaf11Bean> sources) {
        if (sources == null) {
            return null;
        }
        Set<ViewOfLeaf11Bean> results = new HashSet<>();
        for (Leaf11Bean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<ViewOfLeaf11Bean> read(Stack<Leaf11Bean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<ViewOfLeaf11Bean> results = new Stack<>();
        for (Leaf11Bean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, ViewOfLeaf11Bean> read(Map<K, Leaf11Bean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, ViewOfLeaf11Bean> results = new HashMap<>();
        for (Map.Entry<K, Leaf11Bean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public Class<? extends Annotation> getB() {
        return this.b;
    }

    public List<? extends String> getC() {
        return this.c;
    }

    public long getD() {
        return this.d;
    }

    public String getE() {
        return this.e;
    }

    protected void setB(Class<? extends Annotation> b) {
        this.b = b;
    }

    protected void setC(List<? extends String> c) {
        this.c = c;
    }

    protected void setD(long d) {
        this.d = d;
    }

    protected void setE(String e) {
        this.e = e;
    }

}
