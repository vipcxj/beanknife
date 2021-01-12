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

@GeneratedView(targetClass = Leaf21Bean.class, configClass = Leaf21BeanViewConfigure.class)
public class Leaf21BeanDto implements Serializable {
    private static final long serialVersionUID = 0L;

    private List<?> c;

    public Leaf21BeanDto() { }

    public Leaf21BeanDto(
        List<?> c
    ) {
        this.c = c;
    }

    public Leaf21BeanDto(Leaf21BeanDto source) {
        this.c = source.c;
    }

    public static Leaf21BeanDto read(Leaf21Bean source) {
        if (source == null) {
            return null;
        }
        Leaf21BeanDto out = new Leaf21BeanDto();
        out.c = source.getC();
        return out;
    }

    public static Leaf21BeanDto[] read(Leaf21Bean[] sources) {
        if (sources == null) {
            return null;
        }
        Leaf21BeanDto[] results = new Leaf21BeanDto[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<Leaf21BeanDto> read(List<Leaf21Bean> sources) {
        if (sources == null) {
            return null;
        }
        List<Leaf21BeanDto> results = new ArrayList<>();
        for (Leaf21Bean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<Leaf21BeanDto> read(Set<Leaf21Bean> sources) {
        if (sources == null) {
            return null;
        }
        Set<Leaf21BeanDto> results = new HashSet<>();
        for (Leaf21Bean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<Leaf21BeanDto> read(Stack<Leaf21Bean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<Leaf21BeanDto> results = new Stack<>();
        for (Leaf21Bean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, Leaf21BeanDto> read(Map<K, Leaf21Bean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, Leaf21BeanDto> results = new HashMap<>();
        for (Map.Entry<K, Leaf21Bean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public List<?> getC() {
        return this.c;
    }

}
