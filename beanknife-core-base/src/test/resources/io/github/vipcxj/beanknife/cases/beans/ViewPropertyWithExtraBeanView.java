package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = ViewPropertyWithExtraBean.class, configClass = ViewPropertyWithExtraContainerBeanViewConfig.class)
public class ViewPropertyWithExtraBeanView {

    public ViewPropertyWithExtraBeanView() { }

    public ViewPropertyWithExtraBeanView(ViewPropertyWithExtraBeanView source) {
    }

    public ViewPropertyWithExtraBeanView(ViewPropertyWithExtraBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.ViewPropertyWithExtraBeanView should not be null.");
        }
    }

    public static ViewPropertyWithExtraBeanView read(ViewPropertyWithExtraBean source) {
        if (source == null) {
            return null;
        }
        return new ViewPropertyWithExtraBeanView(source);
    }

    public static ViewPropertyWithExtraBeanView[] read(ViewPropertyWithExtraBean[] sources) {
        if (sources == null) {
            return null;
        }
        ViewPropertyWithExtraBeanView[] results = new ViewPropertyWithExtraBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<ViewPropertyWithExtraBeanView> read(List<ViewPropertyWithExtraBean> sources) {
        if (sources == null) {
            return null;
        }
        List<ViewPropertyWithExtraBeanView> results = new ArrayList<>();
        for (ViewPropertyWithExtraBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<ViewPropertyWithExtraBeanView> read(Set<ViewPropertyWithExtraBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<ViewPropertyWithExtraBeanView> results = new HashSet<>();
        for (ViewPropertyWithExtraBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<ViewPropertyWithExtraBeanView> read(Stack<ViewPropertyWithExtraBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<ViewPropertyWithExtraBeanView> results = new Stack<>();
        for (ViewPropertyWithExtraBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, ViewPropertyWithExtraBeanView> read(Map<K, ViewPropertyWithExtraBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, ViewPropertyWithExtraBeanView> results = new HashMap<>();
        for (Map.Entry<K, ViewPropertyWithExtraBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public static String error0() {
        return "Unable to convert from io.github.vipcxj.beanknife.cases.beans.SimpleBean to its view type io.github.vipcxj.beanknife.cases.beans.ExtraParamsBeanView. Because it has extra properties or extra params.";    }

    public static String error1() {
        return "Unable to convert from io.github.vipcxj.beanknife.cases.beans.SimpleBean to its view type io.github.vipcxj.beanknife.cases.beans.ExtraProperties1BeanView. Because it has extra properties or extra params.";    }

    public static String error2() {
        return "Unable to convert from io.github.vipcxj.beanknife.cases.beans.SimpleBean to its view type io.github.vipcxj.beanknife.cases.beans.ExtraProperties2BeanView. Because it has extra properties or extra params.";    }

}
