package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = ViewOfInNestBean.Bean1.Bean2.Bean3.class, configClass = ViewOfInNestBean.Bean1.Bean2.Bean3.class)
public class ViewOfInNestBean$Bean1$Bean2$Bean3View {

    public ViewOfInNestBean$Bean1$Bean2$Bean3View() { }

    public ViewOfInNestBean$Bean1$Bean2$Bean3View(ViewOfInNestBean$Bean1$Bean2$Bean3View source) {
    }

    public static ViewOfInNestBean$Bean1$Bean2$Bean3View read(ViewOfInNestBean.Bean1.Bean2.Bean3 source) {
        if (source == null) {
            return null;
        }
        ViewOfInNestBean$Bean1$Bean2$Bean3View out = new ViewOfInNestBean$Bean1$Bean2$Bean3View();
        return out;
    }

    public static ViewOfInNestBean$Bean1$Bean2$Bean3View[] read(ViewOfInNestBean.Bean1.Bean2.Bean3[] sources) {
        if (sources == null) {
            return null;
        }
        ViewOfInNestBean$Bean1$Bean2$Bean3View[] results = new ViewOfInNestBean$Bean1$Bean2$Bean3View[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<ViewOfInNestBean$Bean1$Bean2$Bean3View> read(List<ViewOfInNestBean.Bean1.Bean2.Bean3> sources) {
        if (sources == null) {
            return null;
        }
        List<ViewOfInNestBean$Bean1$Bean2$Bean3View> results = new ArrayList<>();
        for (ViewOfInNestBean.Bean1.Bean2.Bean3 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<ViewOfInNestBean$Bean1$Bean2$Bean3View> read(Set<ViewOfInNestBean.Bean1.Bean2.Bean3> sources) {
        if (sources == null) {
            return null;
        }
        Set<ViewOfInNestBean$Bean1$Bean2$Bean3View> results = new HashSet<>();
        for (ViewOfInNestBean.Bean1.Bean2.Bean3 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<ViewOfInNestBean$Bean1$Bean2$Bean3View> read(Stack<ViewOfInNestBean.Bean1.Bean2.Bean3> sources) {
        if (sources == null) {
            return null;
        }
        Stack<ViewOfInNestBean$Bean1$Bean2$Bean3View> results = new Stack<>();
        for (ViewOfInNestBean.Bean1.Bean2.Bean3 source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, ViewOfInNestBean$Bean1$Bean2$Bean3View> read(Map<K, ViewOfInNestBean.Bean1.Bean2.Bean3> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, ViewOfInNestBean$Bean1$Bean2$Bean3View> results = new HashMap<>();
        for (Map.Entry<K, ViewOfInNestBean.Bean1.Bean2.Bean3> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

}
