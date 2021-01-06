package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = CommentBean.class, configClass = CommentBean.class)
public class CommentBeanView {

        private String a;

        private String b;

        private long c;

    public CommentBeanView() { }

    public CommentBeanView(
        String a,
        String b,
        long c
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public CommentBeanView(CommentBeanView source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
    }

    public CommentBeanView(CommentBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.CommentBeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
    }

    public static CommentBeanView read(CommentBean source) {
        if (source == null) {
            return null;
        }
        return new CommentBeanView(source);
    }

    public static CommentBeanView[] read(CommentBean[] sources) {
        if (sources == null) {
            return null;
        }
        CommentBeanView[] results = new CommentBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<CommentBeanView> read(List<CommentBean> sources) {
        if (sources == null) {
            return null;
        }
        List<CommentBeanView> results = new ArrayList<>();
        for (CommentBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<CommentBeanView> read(Set<CommentBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<CommentBeanView> results = new HashSet<>();
        for (CommentBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<CommentBeanView> read(Stack<CommentBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<CommentBeanView> results = new Stack<>();
        for (CommentBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, CommentBeanView> read(Map<K, CommentBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, CommentBeanView> results = new HashMap<>();
        for (Map.Entry<K, CommentBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

        /**
     *  this is a.
     *  this is the second line.
     *  {@link Object} this is some doc annotation.
     */
    public String getA() {
        return this.a;
    }

        /**
     *  this is b.
     *  this is the second line.
     *  {@link Object} this is some doc annotation.
     */
    public String getB() {
        return this.b;
    }

        /**
     *  this is c
     *  this comment is on the method.
     *  this is the second line.
     *  {@link Object} this is some doc annotation.
     */
    public long getC() {
        return this.c;
    }

}
