package io.github.vipcxj.beanknife.core.utils;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.runtime.annotations.Access;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

public class LombokUtils {
    public static final String ANNOTATION_GETTER = "lombok.Getter";
    public static final String ANNOTATION_SETTER = "lombok.Setter";

    private static Access fromLombokAccessLevel(String value) {
        switch (value) {
            case "lombok.AccessLevel.PUBLIC":
            case "lombok.AccessLevel.MODULE":
                return Access.PUBLIC;
            case "lombok.AccessLevel.PROTECTED":
                return Access.PROTECTED;
            case "lombok.AccessLevel.PACKAGE":
                return Access.DEFAULT;
            case "lombok.AccessLevel.PRIVATE":
                return Access.PRIVATE;
            case "lombok.AccessLevel.NONE":
                return Access.NONE;
            default:
                throw new IllegalArgumentException("Unsupported lombok getter access level: " + value + ".");
        }
    }

    private static Access getGetterOrSetterAccess(@NonNull Element element, String annotationType, @CheckForNull Access baseAccess) {
        AnnotationMirror annotationMirror = Utils.getAnnotationDirectOn(element, annotationType);
        if (annotationMirror == null) {
            return baseAccess != null ? baseAccess : Access.NONE;
        }
        String value = Utils.getEnumAnnotationValue(annotationMirror, "value");
        if (value == null) {
            return Access.PUBLIC;
        }
        return fromLombokAccessLevel(value);
    }

    public static Access getGetterAccess(@NonNull Element element, @CheckForNull Access baseAccess) {
        return getGetterOrSetterAccess(element, ANNOTATION_GETTER, baseAccess);
    }

    public static Access getSetterAccess(@NonNull Element element, @CheckForNull Access baseAccess) {
        return getGetterOrSetterAccess(element, ANNOTATION_SETTER, baseAccess);
    }
}
