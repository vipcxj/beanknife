package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.annotations.Access;
import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import java.util.Map;

public class ViewOfData {
    private DeclaredType value;
    private String genPackage;
    private String genName;
    private Modifier access;
    private String[] includes;
    private String[] excludes;
    private String includePattern;
    private String excludePattern;
    private Modifier emptyConstructor;
    private Modifier fieldsConstructor;
    private Modifier copyConstructor;
    private Modifier readMethod;
    private Access getters;
    private Access setters;
    private boolean errorMethods;

    public static ViewOfData read(ProcessingEnvironment environment, AnnotationMirror viewOf) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> annValues = environment.getElementUtils().getElementValuesWithDefaults(viewOf);
        ViewOfData data = new ViewOfData();
        data.value = Utils.getTypeAnnotationValue(viewOf, annValues, "value");
        data.genPackage = Utils.getStringAnnotationValue(viewOf, annValues, "genPackage");
        data.genName = Utils.getStringAnnotationValue(viewOf, annValues, "genName");
        data.access = getModifier(Utils.getEnumAnnotationValue(viewOf, annValues, "access"));
        data.includes = Utils.getStringArrayAnnotationValue(viewOf, annValues, "includes");
        data.excludes = Utils.getStringArrayAnnotationValue(viewOf, annValues, "excludes");
        data.includePattern = Utils.getStringAnnotationValue(viewOf, annValues, "includePattern");
        data.excludePattern = Utils.getStringAnnotationValue(viewOf, annValues, "excludePattern");
        data.emptyConstructor = getModifier(Utils.getEnumAnnotationValue(viewOf, annValues, "emptyConstructor"));
        data.fieldsConstructor = getModifier(Utils.getEnumAnnotationValue(viewOf, annValues, "fieldsConstructor"));
        data.copyConstructor = getModifier(Utils.getEnumAnnotationValue(viewOf, annValues, "copyConstructor"));
        data.readMethod = getModifier(Utils.getEnumAnnotationValue(viewOf, annValues, "readMethod"));
        data.getters = getAccess(Utils.getEnumAnnotationValue(viewOf, annValues, "getters"));
        data.setters = getAccess(Utils.getEnumAnnotationValue(viewOf, annValues, "setters"));
        data.errorMethods = Utils.getBooleanAnnotationValue(viewOf, annValues, "errorMethods");
        return data;
    }

    private static Modifier getModifier(String modifier) {
        if ("io.github.vipcxj.beanknife.annotations.Access.PUBLIC".equals(modifier)) {
            return Modifier.PUBLIC;
        } else if ("io.github.vipcxj.beanknife.annotations.Access.PRIVATE".equals(modifier)) {
            return Modifier.PRIVATE;
        } else if ("io.github.vipcxj.beanknife.annotations.Access.PROTECT".equals(modifier)) {
            return Modifier.PROTECTED;
        } else if ("io.github.vipcxj.beanknife.annotations.Access.DEFAULT".equals(modifier)) {
            return Modifier.DEFAULT;
        } else if ("io.github.vipcxj.beanknife.annotations.Access.NONE".equals(modifier)) {
            return null;
        } else {
            throw new IllegalArgumentException("This is impossible!");
        }
    }

    public static Access getAccess(String qName) {
        if ("io.github.vipcxj.beanknife.annotations.Access.PUBLIC".equals(qName)) {
            return Access.PUBLIC;
        } else if ("io.github.vipcxj.beanknife.annotations.Access.PRIVATE".equals(qName)) {
            return Access.PRIVATE;
        } else if ("io.github.vipcxj.beanknife.annotations.Access.PROTECTED".equals(qName)) {
            return Access.PROTECTED;
        } else if ("io.github.vipcxj.beanknife.annotations.Access.DEFAULT".equals(qName)) {
            return Access.DEFAULT;
        } else if ("io.github.vipcxj.beanknife.annotations.Access.NONE".equals(qName)) {
            return Access.NONE;
        } else if ("io.github.vipcxj.beanknife.annotations.Access.UNKNOWN".equals(qName)) {
            return Access.UNKNOWN;
        } else {
            throw new IllegalArgumentException("This is impossible!");
        }
    }

    public DeclaredType getValue() {
        return value;
    }

    public String getGenPackage() {
        return genPackage;
    }

    public String getGenName() {
        return genName;
    }

    public Modifier getAccess() {
        return access;
    }

    public String[] getIncludes() {
        return includes;
    }

    public String[] getExcludes() {
        return excludes;
    }

    public String getIncludePattern() {
        return includePattern;
    }

    public String getExcludePattern() {
        return excludePattern;
    }

    public Modifier getEmptyConstructor() {
        return emptyConstructor;
    }

    public Modifier getFieldsConstructor() {
        return fieldsConstructor;
    }

    public Modifier getCopyConstructor() {
        return copyConstructor;
    }

    public Modifier getReadMethod() {
        return readMethod;
    }

    public Access getGetters() {
        return getters;
    }

    public Access getSetters() {
        return setters;
    }

    public boolean isErrorMethods() {
        return errorMethods;
    }
}
