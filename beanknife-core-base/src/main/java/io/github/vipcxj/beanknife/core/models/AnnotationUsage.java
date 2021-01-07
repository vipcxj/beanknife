package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.runtime.annotations.UnUseAnnotation;
import io.github.vipcxj.beanknife.runtime.annotations.UnUseAnnotations;
import io.github.vipcxj.beanknife.runtime.annotations.UseAnnotation;
import io.github.vipcxj.beanknife.runtime.annotations.UseAnnotations;
import io.github.vipcxj.beanknife.runtime.utils.AnnotationDest;
import io.github.vipcxj.beanknife.runtime.utils.AnnotationSource;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import java.util.*;

public class AnnotationUsage {
    private Set<AnnotationSource> from;
    private Set<AnnotationDest> dest;

    public static AnnotationUsage from(@NonNull Elements elements, @NonNull AnnotationMirror useAnnotation) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> values = elements.getElementValuesWithDefaults(useAnnotation);
        AnnotationUsage usage = new AnnotationUsage();
        usage.from = new HashSet<>(Arrays.asList(Utils.getEnumArrayAnnotationValue(useAnnotation, values, "from", AnnotationSource.class)));
        usage.dest = new HashSet<>(Arrays.asList(Utils.getEnumArrayAnnotationValue(useAnnotation, values, "dest", AnnotationDest.class)));
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
    public static Set<AnnotationDest> getAnnotationDest(@NonNull Map<String, AnnotationUsage> useAnnotations, @NonNull AnnotationMirror annotation, AnnotationSource source) {
        String name = Utils.getAnnotationName(annotation);
        AnnotationUsage annotationUsage = useAnnotations.get(name);
        if (annotationUsage == null) {
            return null;
        }
        if (annotationUsage.getFrom().contains(source)) {
            return annotationUsage.getDest();
        } else {
            return null;
        }
    }

    public Set<AnnotationSource> getFrom() {
        return from;
    }

    public Set<AnnotationDest> getDest() {
        return dest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnotationUsage that = (AnnotationUsage) o;
        return Objects.equals(from, that.from) &&
                Objects.equals(dest, that.dest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, dest);
    }
}
