package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import java.util.Map;

public class ViewMetaData {

    private String value;
    private String packageName;
    private TypeElement of;

    public static ViewMetaData read(ProcessingEnvironment environment, AnnotationMirror viewMeta, TypeElement configElement) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> annValues = environment.getElementUtils().getElementValuesWithDefaults(viewMeta);
        ViewMetaData data = new ViewMetaData();
        data.value = Utils.getStringAnnotationValue(viewMeta, annValues, "value");
        data.packageName = Utils.getStringAnnotationValue(viewMeta, annValues, "packageName");
        TypeElement of = (TypeElement) Utils.getTypeAnnotationValue(viewMeta, annValues, "of").asElement();
        if (of.getQualifiedName().toString().equals("io.github.vipcxj.beanknife.utils.Self")) {
            of = configElement;
        }
        data.of = of;
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
}
