package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = ViewOfInNestBean.Bean1.class, configClass = ViewOfInNestBean.Bean1.class)
public class ViewOfInNestBean$Bean1View {

    public ViewOfInNestBean$Bean1View() { }

    public ViewOfInNestBean$Bean1View(ViewOfInNestBean$Bean1View source) {
    }

    public static ViewOfInNestBean$Bean1View read(ViewOfInNestBean.Bean1 source) {
        if (source == null) {
            return null;
        }
        ViewOfInNestBean$Bean1View out = new ViewOfInNestBean$Bean1View();
        return out;
    }

    public static ViewOfInNestBean$Bean1View[] read(ViewOfInNestBean.Bean1[] sources) {
        if (sources == null) {
            return null;
        }
        ViewOfInNestBean$Bean1View[] results = new ViewOfInNestBean$Bean1View[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<ViewOfInNestBean$Bean1View> read(List<ViewOfInNestBean.Bean1> sources) {
        if (sources == null) {
            return null;
        }
        List<ViewOfInNestBean$Bean1View> results = new ArrayList<>();
        for (ViewOfInNestBean.Bean1 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<ViewOfInNestBean$Bean1View> read(Set<ViewOfInNestBean.Bean1> sources) {
        if (sources == null) {
            return null;
        }
        Set<ViewOfInNestBean$Bean1View> results = new HashSet<>();
        for (ViewOfInNestBean.Bean1 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<ViewOfInNestBean$Bean1View> read(Stack<ViewOfInNestBean.Bean1> sources) {
        if (sources == null) {
            return null;
        }
        Stack<ViewOfInNestBean$Bean1View> results = new Stack<>();
        for (ViewOfInNestBean.Bean1 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, ViewOfInNestBean$Bean1View> read(Map<K, ViewOfInNestBean.Bean1> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, ViewOfInNestBean$Bean1View> results = new HashMap<>();
        for (Map.Entry<K, ViewOfInNestBean.Bean1> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

}
