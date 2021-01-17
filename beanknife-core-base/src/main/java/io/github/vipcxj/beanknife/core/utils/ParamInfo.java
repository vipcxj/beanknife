package io.github.vipcxj.beanknife.core.utils;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.vipcxj.beanknife.core.models.Property;

import javax.lang.model.element.VariableElement;

public class ParamInfo {
    private final VariableElement var;
    private final boolean source;
    private final boolean extraParam;
    private final boolean propertyParam;
    private final String extraParamName;
    private final Property injectedProperty;

    public ParamInfo(@NonNull VariableElement var, boolean source, boolean extraParam, boolean propertyParam, @NonNull String extraParamName, @Nullable Property injectedProperty) {
        this.var = var;
        this.source = source;
        this.extraParam = extraParam;
        this.propertyParam = propertyParam;
        this.extraParamName = extraParamName;
        this.injectedProperty = injectedProperty;
    }

    public static ParamInfo sourceParam(@NonNull VariableElement var) {
        return new ParamInfo(var, true, false, false, "", null);
    }

    public static ParamInfo extraParam(@NonNull VariableElement var, @NonNull String name) {
        return new ParamInfo(var, false, true, false, name, null);
    }

    public static ParamInfo propertyParam(@NonNull VariableElement var, @NonNull Property property) {
        return new ParamInfo(var, false, false, true, "", property);
    }

    public static ParamInfo unknown(@NonNull VariableElement var) {
        return new ParamInfo(var, false, false, false, "", null);
    }

    public VariableElement getVar() {
        return var;
    }

    public boolean isSource() {
        return source;
    }

    public boolean isExtraParam() {
        return extraParam;
    }

    public boolean isPropertyParam() {
        return propertyParam;
    }

    @NonNull
    public String getExtraParamName() {
        return extraParamName;
    }

    public Property getInjectedProperty() {
        return injectedProperty;
    }

    @NonNull
    public String getParameterName() {
        return var.getSimpleName().toString();
    }

    public boolean isUnknown() {
        return !source && !extraParam && !propertyParam;
    }

    public String getMethodName() {
        return var.getEnclosingElement().getSimpleName().toString();
    }
}