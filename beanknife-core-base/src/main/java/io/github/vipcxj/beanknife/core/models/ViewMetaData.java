package io.github.vipcxj.beanknife.core.models;

import io.github.vipcxj.beanknife.core.utils.AnnotationUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.Map;

public class ViewMetaData {

    private String value;
    private String packageName;
    private TypeElement of;
    private TypeElement config;

    ViewMetaData() { }

    public ViewMetaData(String value, String packageName, TypeElement of, TypeElement config) {
        this.value = value;
        this.packageName = packageName;
        this.of = of;
        this.config = config;
    }

    public static ViewMetaData read(ProcessingEnvironment environment, AnnotationMirror viewMeta, TypeElement configElement) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> annValues = environment.getElementUtils().getElementValuesWithDefaults(viewMeta);
        ViewMetaData data = new ViewMetaData();
        data.value = AnnotationUtils.getStringAnnotationValue(viewMeta, annValues, "value");
        data.packageName = AnnotationUtils.getStringAnnotationValue(viewMeta, annValues, "packageName");
        TypeElement of = (TypeElement) AnnotationUtils.<DeclaredType>getTypeAnnotationValue(viewMeta, annValues, "of").asElement();
        if (of.getQualifiedName().toString().equals("io.github.vipcxj.beanknife.runtime.utils.Self")) {
            of = configElement;
        }
        data.of = of;
        data.config = configElement;
        return data;
    }

    public String getValue() {
        return value;
    }

    public String getPackageName() {
        return packageName;
    }

    public TypeElement getOf() {
        return of;
    }

    public TypeElement getConfig() {
        return config;
    }
}
