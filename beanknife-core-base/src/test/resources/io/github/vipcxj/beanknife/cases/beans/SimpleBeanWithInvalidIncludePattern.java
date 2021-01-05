package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = SimpleBean.class, configClass = InvalidPatternViewConfig.class)
public class SimpleBeanWithInvalidIncludePattern {

    public SimpleBeanWithInvalidIncludePattern() { }

    public SimpleBeanWithInvalidIncludePattern(SimpleBeanWithInvalidIncludePattern source) {
    }

    public SimpleBeanWithInvalidIncludePattern(SimpleBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.SimpleBeanWithInvalidIncludePattern should not be null.");
        }
    }

    public static SimpleBeanWithInvalidIncludePattern read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        return new SimpleBeanWithInvalidIncludePattern(source);
    }

    public static SimpleBeanWithInvalidIncludePattern[] read(SimpleBean[] sources) {
        if (sources == null) {
            return null;
        }
        SimpleBeanWithInvalidIncludePattern[] results = new SimpleBeanWithInvalidIncludePattern[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<SimpleBeanWithInvalidIncludePattern> read(List<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        List<SimpleBeanWithInvalidIncludePattern> results = new ArrayList<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<SimpleBeanWithInvalidIncludePattern> read(Set<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<SimpleBeanWithInvalidIncludePattern> results = new HashSet<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<SimpleBeanWithInvalidIncludePattern> read(Stack<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<SimpleBeanWithInvalidIncludePattern> results = new Stack<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, SimpleBeanWithInvalidIncludePattern> read(Map<K, SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, SimpleBeanWithInvalidIncludePattern> results = new HashMap<>();
        for (Map.Entry<K, SimpleBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public static String error0() {
        return "Invalid include pattern part: \"\\\".\r\nUnexpected internal error near index 1\r\n\\\r\nInclude pattern is a space or comma divided string which each part is a valid regex pattern. For example: \"[aA]pple, [oO]range\" matches apple, Apple, orange and Orange.";    }

}
