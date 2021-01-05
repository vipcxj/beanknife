package io.github.vipcxj.beanknife.runtime.utils;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedMeta;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;

public class Utils {

    public static boolean isGeneratedView(Class<?> type) {
        GeneratedView annotation = type.getAnnotation(GeneratedView.class);
        return annotation != null;
    }

    public static boolean isGeneratedMeta(Class<?> type) {
        GeneratedMeta annotation = type.getAnnotation(GeneratedMeta.class);
        return annotation != null;
    }
}
