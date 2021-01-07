package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.runtime.annotations.UnUseAnnotation;
import io.github.vipcxj.beanknife.runtime.annotations.UnUseAnnotations;
import io.github.vipcxj.beanknife.runtime.annotations.UseAnnotation;
import io.github.vipcxj.beanknife.runtime.annotations.UseAnnotations;
import io.github.vipcxj.beanknife.runtime.utils.AnnotationPos;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import java.util.*;

public class AnnotationUsage {
    private boolean useFromTarget;
    private boolean useFromConfig;
    private Set<AnnotationPos> dest;

    public static AnnotationUsage from(@NonNull Elements elements, @NonNull AnnotationMirror useAnnotation) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> values = elements.getElementValuesWithDefaults(useAnnotation);
        AnnotationUsage usage = new AnnotationUsage();
        usage.useFromTarget = Utils.getBooleanAnnotationValue(useAnnotation, values, "useFromTarget");
        usage.useFromConfig = Utils.getBooleanAnnotationValue(useAnnotation, values, "useFromConfig");
        usage.dest = new HashSet<>(Arrays.asList(Utils.getEnumArrayAnnotationValue(useAnnotation, values, "dest", AnnotationPos.class)));
        return usage;
    }

    @NonNull
    public static Map<String, AnnotationUsage> collectAnnotationUsages(@NonNull Elements elements, @NonNull Element element, @CheckForNull Map<String, AnnotationUsage> baseUsages) {
        Map<String, AnnotationUsage> results = baseUsages != null ? new HashMap<>(baseUsages) : new HashMap<>();
        List<AnnotationMirror> useAnnotations = Utils.getAnnotationsOn(elements, element, UseAnnotation.class, UseAnnotations.class);
        List<AnnotationMirror> unUseAnnotations = Utils.getAnnotationsOn(elements, element, UnUseAnnotation.class, UnUseAnnotations.class);
        for (AnnotationMirror useAnnotation : useAnnotations) {
            AnnotationUsage annotationUsage = AnnotationUsage.from(elements, useAnnotation);
            Map<? extends ExecutableElement, ? extends AnnotationValue> values = elements.getElementValuesWithDefaults(useAnnotation);
            DeclaredType[] types = Utils.getTypeArrayAnnotationValue(useAnnotation, values, "value");
            for (DeclaredType type : types) {
                String key = Utils.toElement(type).getQualifiedName().toString();
                results.put(key, annotationUsage);
            }
        }
        for (AnnotationMirror unUseAnnotation : unUseAnnotations) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> values = elements.getElementValuesWithDefaults(unUseAnnotation);
            DeclaredType[] types = Utils.getTypeArrayAnnotationValue(unUseAnnotation, values, "value");
            for (DeclaredType type : types) {
                String key = Utils.toElement(type).getQualifiedName().toString();
                results.remove(key);
            }
        }
        return results;
    }

    @CheckForNull
    public static Set<AnnotationPos> getAnnotationDest(@NonNull Map<String, AnnotationUsage> useAnnotations, @NonNull AnnotationMirror annotation, boolean fromTarget) {
        String name = Utils.getAnnotationName(annotation);
        AnnotationUsage annotationUsage = useAnnotations.get(name);
        if (annotationUsage == null) {
            return null;
        }
        if (fromTarget && !annotationUsage.isUseFromTarget()) {
            return null;
        }
        if (!fromTarget && !annotationUsage.isUseFromConfig()) {
            return null;
        }
        return annotationUsage.getDest();
    }

    public boolean isUseFromTarget() {
        return useFromTarget;
    }

    public boolean isUseFromConfig() {
        return useFromConfig;
    }

    public Set<AnnotationPos> getDest() {
        return dest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationUsage usage = (AnnotationUsage) o;
        return useFromTarget == usage.useFromTarget &&
                useFromConfig == usage.useFromConfig &&
                dest == usage.dest;
    }

    @Override
    public int hashCode() {
        return Objects.hash(useFromTarget, useFromConfig, dest);
    }
}
