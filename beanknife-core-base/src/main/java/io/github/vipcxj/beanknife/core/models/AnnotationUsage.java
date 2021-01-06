package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.runtime.utils.AnnotationPos;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import java.util.Map;
import java.util.Objects;

public class AnnotationUsage {
    private boolean useFromTarget;
    private boolean useFromConfig;
    private AnnotationPos dest;

    public AnnotationUsage() {
    }

    public AnnotationUsage(AnnotationUsage other) {
        this.useFromTarget = other.useFromTarget;
        this.useFromConfig = other.useFromConfig;
        this.dest = other.dest;
    }

    public static AnnotationUsage from(@NonNull ProcessingEnvironment environment, @NonNull AnnotationMirror useAnnotation) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> values = environment.getElementUtils().getElementValuesWithDefaults(useAnnotation);
        AnnotationUsage usage = new AnnotationUsage();
        usage.useFromTarget = Utils.getBooleanAnnotationValue(useAnnotation, values, "useFromTarget");
        usage.useFromConfig = Utils.getBooleanAnnotationValue(useAnnotation, values, "useFromConfig");
        usage.dest = Utils.getEnumAnnotationValue(useAnnotation, values, "dest", AnnotationPos.class);
        return usage;
    }

    public boolean isUseFromTarget() {
        return useFromTarget;
    }

    public boolean isUseFromConfig() {
        return useFromConfig;
    }

    public AnnotationPos getDest() {
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
