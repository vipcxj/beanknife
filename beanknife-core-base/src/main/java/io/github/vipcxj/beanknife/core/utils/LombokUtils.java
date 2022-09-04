package io.github.vipcxj.beanknife.core.utils;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.runtime.annotations.Access;

import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        boolean hasData = hasDataAnnotation(element);
        AnnotationMirror annotationMirror = Utils.getAnnotationDirectOn(element, annotationType);
        Access defaultAccess = baseAccess != null ? baseAccess : (hasData ? Access.PUBLIC : Access.NONE);
        if (annotationMirror == null) {
            return defaultAccess;
        }
        String value = AnnotationUtils.getEnumAnnotationValue(annotationMirror, "value");
        if (value == null) {
            return Access.PUBLIC;
        }
        return fromLombokAccessLevel(value);
    }

    private static boolean hasDataAnnotation(@NonNull Element element) {
        return Utils.getAnnotationDirectOn(element, "lombok.Data") != null;
    }

    public static Access getGetterAccess(@NonNull Element element, @CheckForNull Access baseAccess) {
        return getGetterOrSetterAccess(element, ANNOTATION_GETTER, baseAccess);
    }

    public static Access getSetterAccess(@NonNull Element element, @CheckForNull Access baseAccess) {
        return getGetterOrSetterAccess(element, ANNOTATION_SETTER, baseAccess);
    }

    public static List<VariableElement> getAllLombokFields(TypeElement typeElement) {
        TypeMirror superType = typeElement.getSuperclass();
        if (superType.getKind() == TypeKind.NONE) {
            return Collections.emptyList();
        }
        Access baseGetterAccess = getGetterAccess(typeElement, null);
        Access baseSetterAccess = getSetterAccess(typeElement, null);
        List<VariableElement> superFields = getAllLombokFields(Utils.toElement((DeclaredType) superType));
        List<VariableElement> fields = new ArrayList<>();
        for (Element element : typeElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.FIELD) {
                Access getterAccess = getGetterAccess(element, baseGetterAccess);
                Access setterAccess = getSetterAccess(element, baseSetterAccess);
                if ((Access.canSeeFromSubClass(getterAccess)) || Access.canSeeFromSubClass(setterAccess)) {
                    fields.add((VariableElement) element);
                }
            }
        }
        List<VariableElement> result = new ArrayList<>();
        for (VariableElement superField : superFields) {
            if (fields.stream().anyMatch(f -> f.getSimpleName().contentEquals(superField.getSimpleName()))) {
                continue;
            }
            result.add(superField);
        }
        result.addAll(fields);
        return result;
    }
}
