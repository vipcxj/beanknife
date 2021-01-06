package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.DocumentedTypeAnnotation;
import io.github.vipcxj.beanknife.cases.annotations.InheritableTypeAnnotation;
import io.github.vipcxj.beanknife.cases.annotations.TypeAnnotation;
import io.github.vipcxj.beanknife.cases.annotations.ValueAnnotation;
import io.github.vipcxj.beanknife.cases.models.AEnum;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = AnnotationBean.class, configClass = AnnotationBeanViewConfigure.class)
@InheritableTypeAnnotation(
    annotation = @ValueAnnotation(type = {
        int.class
    }),
    annotations = {
        @ValueAnnotation(type = {
            void.class
        }),
        @ValueAnnotation(type = {
            String.class
        }),
        @ValueAnnotation(type = {
            int[][][].class
        }),
        @ValueAnnotation(type = {
            Void.class
        }),
        @ValueAnnotation(type = {
            Void[].class
        })
    }
)
@TypeAnnotation(Date.class)
@DocumentedTypeAnnotation(
    enumValue = AEnum.B,
    enumValues = {
        AEnum.C,
        AEnum.B,
        AEnum.A
    }
)
public class AnnotationBeanView {

    public AnnotationBeanView() { }

    public AnnotationBeanView(AnnotationBeanView source) {
    }

    public AnnotationBeanView(AnnotationBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.AnnotationBeanView should not be null.");
        }
    }

    public static AnnotationBeanView read(AnnotationBean source) {
        if (source == null) {
            return null;
        }
        return new AnnotationBeanView(source);
    }

    public static AnnotationBeanView[] read(AnnotationBean[] sources) {
        if (sources == null) {
            return null;
        }
        AnnotationBeanView[] results = new AnnotationBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<AnnotationBeanView> read(List<AnnotationBean> sources) {
        if (sources == null) {
            return null;
        }
        List<AnnotationBeanView> results = new ArrayList<>();
        for (AnnotationBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<AnnotationBeanView> read(Set<AnnotationBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<AnnotationBeanView> results = new HashSet<>();
        for (AnnotationBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<AnnotationBeanView> read(Stack<AnnotationBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<AnnotationBeanView> results = new Stack<>();
        for (AnnotationBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, AnnotationBeanView> read(Map<K, AnnotationBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, AnnotationBeanView> results = new HashMap<>();
        for (Map.Entry<K, AnnotationBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

}
