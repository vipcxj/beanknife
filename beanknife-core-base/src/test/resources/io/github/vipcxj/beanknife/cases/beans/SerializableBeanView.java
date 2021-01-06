package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = SerializableBean.class, configClass = SerializableBean.class)
public class SerializableBeanView implements Serializable {
    private static final long serialVersionUID = 4L;

        private String a;

        private long b;

    public SerializableBeanView() { }

    public SerializableBeanView(
        String a,
        long b
    ) {
        this.a = a;
        this.b = b;
    }

    public SerializableBeanView(SerializableBeanView source) {
        this.a = source.a;
        this.b = source.b;
    }

    public SerializableBeanView(SerializableBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.SerializableBeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
    }

    public static SerializableBeanView read(SerializableBean source) {
        if (source == null) {
            return null;
        }
        return new SerializableBeanView(source);
    }

    public static SerializableBeanView[] read(SerializableBean[] sources) {
        if (sources == null) {
            return null;
        }
        SerializableBeanView[] results = new SerializableBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<SerializableBeanView> read(List<SerializableBean> sources) {
        if (sources == null) {
            return null;
        }
        List<SerializableBeanView> results = new ArrayList<>();
        for (SerializableBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<SerializableBeanView> read(Set<SerializableBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<SerializableBeanView> results = new HashSet<>();
        for (SerializableBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<SerializableBeanView> read(Stack<SerializableBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<SerializableBeanView> results = new Stack<>();
        for (SerializableBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, SerializableBeanView> read(Map<K, SerializableBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, SerializableBeanView> results = new HashMap<>();
        for (Map.Entry<K, SerializableBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

        public String getA() {
        return this.a;
    }

        public long getB() {
        return this.b;
    }

}
