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

@GeneratedView(targetClass = Leaf12Bean.class, configClass = Leaf12BeanViewConfigure.class)
public class ViewOfLeaf12Bean implements Serializable {
    private static final long serialVersionUID = 0L;

    private Class<? extends Annotation> b;

    private List<? extends String> c;

    private long d;

    private String e;

    public ViewOfLeaf12Bean() { }

    public ViewOfLeaf12Bean(
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

    public ViewOfLeaf12Bean(ViewOfLeaf12Bean source) {
        this.b = source.b;
        this.c = source.c;
        this.d = source.d;
        this.e = source.e;
    }

    public static ViewOfLeaf12Bean read(Leaf12Bean source) {
        if (source == null) {
            return null;
        }
        ViewOfLeaf12Bean out = new ViewOfLeaf12Bean();
        out.b = source.getB();
        out.c = source.getC();
        out.d = source.getD();
        out.e = source.getE();
        return out;
    }

    public static ViewOfLeaf12Bean[] read(Leaf12Bean[] sources) {
        if (sources == null) {
            return null;
        }
        ViewOfLeaf12Bean[] results = new ViewOfLeaf12Bean[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<ViewOfLeaf12Bean> read(List<Leaf12Bean> sources) {
        if (sources == null) {
            return null;
        }
        List<ViewOfLeaf12Bean> results = new ArrayList<>();
        for (Leaf12Bean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<ViewOfLeaf12Bean> read(Set<Leaf12Bean> sources) {
        if (sources == null) {
            return null;
        }
        Set<ViewOfLeaf12Bean> results = new HashSet<>();
        for (Leaf12Bean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<ViewOfLeaf12Bean> read(Stack<Leaf12Bean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<ViewOfLeaf12Bean> results = new Stack<>();
        for (Leaf12Bean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, ViewOfLeaf12Bean> read(Map<K, Leaf12Bean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, ViewOfLeaf12Bean> results = new HashMap<>();
        for (Map.Entry<K, Leaf12Bean> source : sources.entrySet()) {
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
