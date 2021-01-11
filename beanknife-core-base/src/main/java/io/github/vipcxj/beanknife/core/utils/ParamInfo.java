package io.github.vipcxj.beanknife.core.utils;

import edu.umd.cs.findbugs.annotations.NonNull;

import javax.lang.model.element.VariableElement;

public class ParamInfo {
    private final VariableElement var;
    private final boolean source;
    private final boolean extraParam;
    private final String extraParamName;

    public ParamInfo(@NonNull VariableElement var, boolean source, boolean extraParam, @NonNull String extraParamName) {
        this.var = var;
        this.source = source;
        this.extraParam = extraParam;
        this.extraParamName = extraParamName;
    }

    public static ParamInfo sourceParam(@NonNull VariableElement var) {
        return new ParamInfo(var, true, false, "");
    }

    public static ParamInfo extraParam(@NonNull VariableElement var, @NonNull String name) {
        return new ParamInfo(var, false, true, name);
    }

    public static ParamInfo unknown(@NonNull VariableElement var) {
        return new ParamInfo(var, false, false, "");
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

    @NonNull
    public String getExtraParamName() {
        return extraParamName;
    }

    @NonNull
    public String getParameterName() {
        return var.getSimpleName().toString();
    }

    public boolean isUnknown() {
        return !source && !extraParam;
    }

    public String getMethodName() {
        return var.getEnclosingElement().getSimpleName().toString();
    }
}