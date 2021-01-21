package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.runtime.annotations.*;
import io.github.vipcxj.beanknife.runtime.utils.AnnotationDest;
import io.github.vipcxj.beanknife.runtime.utils.AnnotationSource;
import io.github.vipcxj.beanknife.runtime.utils.CacheType;
import io.github.vipcxj.beanknife.runtime.utils.Self;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.util.Elements;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.*;
import java.util.stream.Collectors;

public class ViewOfData {
    private AnnotationMirror viewOf;
    private TypeElement targetElement;
    private TypeElement configElement;
    private TypeElement sourceElement;
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
    private Modifier readConstructor;
    private Access getters;
    private Access setters;
    private boolean errorMethods;
    private boolean serializable;
    private long serialVersionUID;
    private boolean useDefaultBeanProvider;
    private CacheType configureBeanCacheType;
    private Access writeBackMethod;
    private Access createAndWriteBackMethod;
    private Set<String> extraExcludes;
    private Set<String> writeExcludes;
    private Map<String, AnnotationUsage> useAnnotations;
    private List<AnnotationMirror> annotationMirrors;
    private Set<String> annotationNames;

    public static ViewOfData read(@NonNull ProcessingEnvironment environment, @NonNull AnnotationMirror viewOf, @NonNull TypeElement sourceElement) {
        ViewOfData data = new ViewOfData();
        data.load(environment, viewOf, sourceElement);
        return data;
    }

    public void load(@NonNull ProcessingEnvironment environment, @NonNull AnnotationMirror viewOf, @NonNull TypeElement sourceElement) {
        Elements elements = environment.getElementUtils();
        Map<? extends ExecutableElement, ? extends AnnotationValue> annValues = elements.getElementValuesWithDefaults(viewOf);
        this.viewOf = viewOf;
        this.sourceElement = sourceElement;
        DeclaredType value = Utils.getTypeAnnotationValue(viewOf, annValues, "value");
        this.targetElement = (TypeElement) value.asElement();
        if (Utils.isThisTypeElement(this.targetElement, Self.class)) {
            this.targetElement = sourceElement;
        }
        DeclaredType config = Utils.getTypeAnnotationValue(viewOf, annValues, "config");
        this.configElement = (TypeElement) config.asElement();
        if (Utils.isThisTypeElement(this.configElement, Self.class)) {
            this.configElement = sourceElement;
        }
        this.genPackage = Utils.getStringAnnotationValue(viewOf, annValues, "genPackage");
        this.genName = loadGenName(elements);
        this.access = getModifier(loadEnum(elements, "access", Access.PUBLIC, Access.class, ViewAccess.class));
        this.includes = loadStringArray(elements, "includes", ViewPropertiesInclude.class, ViewPropertiesIncludes.class);
        this.excludes = loadStringArray(elements, "excludes", ViewPropertiesExclude.class, ViewPropertiesExcludes.class);
        this.includePattern = loadPattern(elements, "includePattern", ViewPropertiesIncludePattern.class, ViewPropertiesIncludePatterns.class);
        this.excludePattern = loadPattern(elements, "excludePattern", ViewPropertiesExcludePattern.class, ViewPropertiesExcludePatterns.class);
        this.emptyConstructor = getModifier(loadEnum(elements, "emptyConstructor", Access.PUBLIC, Access.class, ViewEmptyConstructor.class));
        this.fieldsConstructor = getModifier(loadEnum(elements, "fieldsConstructor", Access.PUBLIC, Access.class, ViewFieldsConstructor.class));
        this.copyConstructor = getModifier(loadEnum(elements, "copyConstructor", Access.PUBLIC, Access.class, ViewCopyConstructor.class));
        this.readConstructor = getModifier(loadEnum(elements, "readConstructor", Access.PUBLIC, Access.class, ViewReadConstructor.class));
        this.getters = loadEnum(elements, "getters", Access.PUBLIC, Access.class, ViewGetters.class);
        this.setters = loadEnum(elements, "setters", Access.NONE, Access.class, ViewSetters.class);
        this.errorMethods = loadBoolean(elements, "errorMethods", true, ViewErrorMethods.class);
        this.serializable = loadBoolean(elements, "serializable", false, ViewSerializable.class);
        this.serialVersionUID = loadSerialVersionUID(elements);
        this.useDefaultBeanProvider = loadBoolean(elements, "useDefaultBeanProvider", false, ViewUseDefaultBeanProvider.class);
        this.configureBeanCacheType = loadEnum(elements, "configureBeanCacheType", CacheType.LOCAL, CacheType.class, ViewConfigureBeanCacheType.class);
        this.writeBackMethod = loadEnum(elements, "writeBackMethod", Access.NONE, Access.class, ViewWriteBackMethod.class);
        this.createAndWriteBackMethod = loadEnum(elements, "createAndWriteBackMethod", Access.NONE, Access.class, ViewCreateAndWriteBackMethod.class);
        this.extraExcludes = new HashSet<>();
        List<AnnotationMirror> removeViewProperties = Utils.getAnnotationsOn(elements, configElement, RemoveViewProperty.class, RemoveViewProperties.class);
        for (AnnotationMirror removeViewProperty : removeViewProperties) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> values = elements.getElementValuesWithDefaults(removeViewProperty);
            String[] exclude = Utils.getStringArrayAnnotationValue(removeViewProperty, values, "value");
            this.extraExcludes.addAll(Arrays.asList(exclude));
        }
        this.writeExcludes = new HashSet<>();
        List<AnnotationMirror> writeBackExcludes = Utils.getAnnotationsOn(elements, configElement, ViewWriteBackExclude.class, ViewWriteBackExcludes.class);
        for (AnnotationMirror writeBackExclude : writeBackExcludes) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> values = writeBackExclude.getElementValues();
            String[] exclude = Utils.getStringArrayAnnotationValue(writeBackExclude, values, "value");
            this.writeExcludes.addAll(Arrays.asList(exclude));
        }
        this.useAnnotations = AnnotationUsage.collectAnnotationUsages(elements, configElement, null);
        collectAnnotations(elements);
    }

    private String loadGenName(Elements elements) {
        String genName = Utils.getStringAnnotationValue(viewOf, "genName");
        if (genName != null ) {
            return genName;
        }
        List<AnnotationMirror> mappers = Utils.getAnnotationsOn(elements, configElement, ViewGenNameMapper.class, null, true, false);
        if (!mappers.isEmpty()) {
            AnnotationMirror mapperAnnotation = mappers.get(mappers.size() - 1);
            String mapper = Utils.getStringAnnotationValue(mapperAnnotation, "value");
            if (mapper != null) {
                return mapper.replaceAll("\\$\\{name}", targetElement.getSimpleName().toString());
            }
        }
        return "";
    }

    private <T extends Enum<T>> T loadEnum(Elements elements, String name, T defaultValue, Class<T> enumType, Class<? extends Annotation> annotationType) {
        T access = Utils.getEnumAnnotationValue(viewOf, name, enumType);
        if (access == null) {
            List<AnnotationMirror> accessAnnotations = Utils.getAnnotationsOn(elements, configElement, annotationType, null, true, false);
            if (!accessAnnotations.isEmpty()) {
                access = Utils.getEnumAnnotationValue(accessAnnotations.get(accessAnnotations.size() - 1), "value", enumType);
            }
        }
        if (access == null) {
            access = defaultValue;
        }
        return access;
    }

    private boolean loadBoolean(Elements elements, String name, boolean defaultValue, Class<? extends Annotation> annotationType) {
        Boolean objValue = Utils.getBooleanAnnotationValue(viewOf, name);
        if (objValue == null) {
            List<AnnotationMirror> annotations = Utils.getAnnotationsOn(elements, configElement, annotationType, null, true, false);
            if (!annotations.isEmpty()) {
                objValue = Utils.getBooleanAnnotationValue(annotations.get(annotations.size() - 1), "value");
            }
        }
        return objValue != null ? objValue : defaultValue;
    }

    private long loadSerialVersionUID(Elements elements) {
        Long objValue = Utils.getLongAnnotationValue(viewOf, "serialVersionUID");
        if (objValue == null) {
            List<AnnotationMirror> annotations = Utils.getAnnotationsOn(elements, configElement, ViewSerialVersionUID.class, null, true, false);
            if (!annotations.isEmpty()) {
                objValue = Utils.getLongAnnotationValue(annotations.get(annotations.size() - 1), "value");
            }
        }
        return objValue != null ? objValue : 0L;
    }

    private String[] loadStringArray(Elements elements, String name, Class<? extends Annotation> annotationType, Class<? extends Annotation> annotationsType) {
        List<String> values = Utils.getStringArrayAnnotationValue(viewOf, name);
        if (values == null) {
            values = new ArrayList<>();
            List<AnnotationMirror> annotations = Utils.getAnnotationsOn(elements, configElement, annotationType, annotationsType, true, false);
            for (AnnotationMirror annotation : annotations) {
                List<String> value = Utils.getStringArrayAnnotationValue(annotation, "value");
                if (value != null) {
                    values.addAll(value);
                }
            }
        }
        return values.toArray(new String[0]);
    }

    private String loadPattern(Elements elements, String name, Class<? extends Annotation> annotationType, Class<? extends Annotation> annotationsType) {
        String value = Utils.getStringAnnotationValue(viewOf, name);
        if (value == null) {
            StringBuilder sb = new StringBuilder();
            List<AnnotationMirror> annotations = Utils.getAnnotationsOn(elements, configElement, annotationType, annotationsType, true, false);
            for (AnnotationMirror annotation : annotations) {
                String part = Utils.getStringAnnotationValue(annotation, "value");
                if (part != null) {
                    if (sb.length() == 0) {
                        sb.append(part);
                    } else {
                        sb.append(" ").append(part);
                    }
                }
            }
            value = sb.toString();
        }
        return value;
    }

    private void addAnnotation(@NonNull Elements elements, @NonNull AnnotationMirror annotationMirror, AnnotationSource source) {
        List<AnnotationMirror> annotationComponents = Utils.getRepeatableAnnotationComponents(elements, annotationMirror);
        if (annotationComponents == null) {
            Set<AnnotationDest> dest = AnnotationUsage.getAnnotationDest(useAnnotations, annotationMirror, source);
            if (dest != null && (dest.contains(AnnotationDest.SAME) || dest.contains(AnnotationDest.TYPE)) && Utils.annotationCanPutOn(elements, annotationMirror, ElementType.TYPE)) {
                String annotationName = Utils.getAnnotationName(annotationMirror);
                if (Utils.isAnnotationRepeatable(elements, annotationMirror)) {
                    this.annotationMirrors.add(annotationMirror);
                    this.annotationNames.add(annotationName);
                } else if (!this.annotationNames.contains(annotationName)){
                    this.annotationMirrors.add(annotationMirror);
                    this.annotationNames.add(annotationName);
                }
            }
        } else {
            for (AnnotationMirror annotationComponent : annotationComponents) {
                Set<AnnotationDest> dest = AnnotationUsage.getAnnotationDest(useAnnotations, annotationComponent, source);
                if (dest != null && (dest.contains(AnnotationDest.SAME) || dest.contains(AnnotationDest.TYPE)) && Utils.annotationCanPutOn(elements, annotationMirror, ElementType.TYPE)) {
                    String annotationName = Utils.getAnnotationName(annotationComponent);
                    this.annotationMirrors.add(annotationComponent);
                    this.annotationNames.add(annotationName);
                }
            }
        }
    }

    private void collectAnnotations(Elements elements) {
        this.annotationMirrors = new ArrayList<>();
        this.annotationNames = new HashSet<>();
        List<AnnotationMirror> annotationMirrors = Utils.getAllAnnotationMirrors(elements, this.targetElement);
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            addAnnotation(elements, annotationMirror, AnnotationSource.TARGET_TYPE);
        }
        annotationMirrors = Utils.getAllAnnotationMirrors(elements, this.configElement);
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            addAnnotation(elements, annotationMirror, AnnotationSource.CONFIG);
        }
    }

    private static Modifier getModifier(Access access) {
        switch (access) {
            case PUBLIC:
                return Modifier.PUBLIC;
            case PRIVATE:
                return Modifier.PRIVATE;
            case PROTECTED:
                return Modifier.PROTECTED;
            case DEFAULT:
                return Modifier.DEFAULT;
            default:
                return null;
        }
    }

    public TypeElement getTargetElement() {
        return targetElement;
    }

    public void setTargetElement(TypeElement targetElement) {
        this.targetElement = targetElement;
    }

    public TypeElement getConfigElement() {
        return configElement;
    }

    public void setConfigElement(TypeElement configElement) {
        this.configElement = configElement;
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

    public Modifier getReadConstructor() {
        return readConstructor;
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

    public boolean isSerializable() {
        return serializable;
    }

    public long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isUseDefaultBeanProvider() {
        return useDefaultBeanProvider;
    }

    public CacheType getConfigureBeanCacheType() {
        return configureBeanCacheType;
    }

    public Access getWriteBackMethod() {
        return writeBackMethod;
    }

    public Access getCreateAndWriteBackMethod() {
        return createAndWriteBackMethod;
    }

    public Set<String> getExtraExcludes() {
        return extraExcludes;
    }

    public Set<String> getWriteExcludes() {
        return writeExcludes;
    }

    public Map<String, AnnotationUsage> getUseAnnotations() {
        return useAnnotations;
    }

    public List<AnnotationMirror> getAnnotationMirrors() {
        return annotationMirrors;
    }

    private static void printStringAnnotationValue(PrintWriter writer, String name, String value, String indent, int indentNum) {
        if (!value.isEmpty()) {
            Utils.printIndent(writer, indent, indentNum);
            writer.print(name);
            writer.print(" = \"");
            writer.print(StringEscapeUtils.escapeJava(value));
            writer.println("\",");
        }
    }

    private static void printAnnotationValue(PrintWriter writer, String name, Object value, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        writer.print(name);
        writer.print(" = ");
        writer.print(value);
        writer.println(",");
    }

    private static void printStringArrayAnnotationValue(PrintWriter writer, String name, String[] values, String indent, int indentNum) {
        if (values.length > 0 && values.length < 6) {
            Utils.printIndent(writer, indent, indentNum);
            writer.print(name);
            writer.print(" = {");
            writer.print(
                    Arrays.stream(values)
                            .map(include -> "\"" + StringEscapeUtils.escapeJava(include) + "\"")
                            .collect(Collectors.joining(", "))
            );
            writer.println(" },");
        } else if (values.length > 0) {
            Utils.printIndent(writer, indent, indentNum);
            writer.print(name);
            writer.println(" = {");
            int i = 0;
            for (String include : values) {
                Utils.printIndent(writer, indent, indentNum + 1);
                writer.print("\"");
                writer.print(StringEscapeUtils.escapeJava(include));
                writer.print("\"");
                if (i != values.length - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
                ++i;
            }
            Utils.printIndent(writer, indent, indentNum);
            writer.println("}");
        }
    }

    public static void printEnumAnnotationValue(PrintWriter writer, String name, Type enumType, Enum<?> enumValue, Context context, String indent, int indentNum) {
        printEnumAnnotationValue(writer, name, enumType, enumValue, context, indent, indentNum, false);
    }

    public static void printEnumAnnotationValue(PrintWriter writer, String name, Type enumType, Enum<?> enumValue, Context context, String indent, int indentNum, boolean end) {
        Utils.printIndent(writer, indent, indentNum);
        writer.print(name);
        writer.print(" = ");
        enumType.printType(writer, context, false, false);
        writer.print(".");
        writer.print(enumValue.name());
        if (!end) {
            writer.println(",");
        }
    }

    private void printElement(@NonNull PrintWriter writer, @NonNull Context context, @NonNull Element element) {
        Type type = Type.extract(context, element);
        if (type == null) {
            context.error("Unable to resolve element: " + element + ".");
            writer.print(element);
        } else {
            type.printType(writer, context, false, false);
        }
    }

    public void print(@NonNull PrintWriter writer, @NonNull Context context, @NonNull String indent, int indentNum) {
        writer.print("@");
        Type viewOfType = Type.extract(context, ViewOf.class);
        viewOfType.printType(writer, context, false, false);
        Type accessType = Objects.requireNonNull(Type.extract(context, Access.class));
        Type cacheTypeType = Objects.requireNonNull(Type.extract(context, CacheType.class));
        writer.println("(");
        Utils.printIndent(writer, indent, indentNum + 1);
        writer.print("value = ");
        printElement(writer, context, targetElement);
        writer.println(".class,");
        Utils.printIndent(writer, indent, indentNum + 1);
        writer.print("config = ");
        printElement(writer, context, configElement);
        writer.println(".class,");
        printStringAnnotationValue(writer, "genPackage", genPackage, indent, indentNum + 1);
        printStringAnnotationValue(writer, "genName", genName, indent, indentNum + 1);
        printEnumAnnotationValue(writer, "access", accessType, Utils.accessFromModifier(this.access), context, indent, indentNum + 1);
        printStringArrayAnnotationValue(writer, "includes", includes, indent, indentNum + 1);
        printStringArrayAnnotationValue(writer, "excludes", excludes, indent, indentNum + 1);
        printStringAnnotationValue(writer, "includePattern", includePattern, indent, indentNum + 1);
        printStringAnnotationValue(writer, "excludePattern", excludePattern, indent, indentNum + 1);
        printEnumAnnotationValue(writer, "emptyConstructor", accessType, Utils.accessFromModifier(emptyConstructor), context, indent, indentNum + 1);
        printEnumAnnotationValue(writer, "fieldsConstructor", accessType, Utils.accessFromModifier(fieldsConstructor), context, indent, indentNum + 1);
        printEnumAnnotationValue(writer, "copyConstructor", accessType, Utils.accessFromModifier(copyConstructor), context, indent, indentNum + 1);
        printEnumAnnotationValue(writer, "readConstructor", accessType, Utils.accessFromModifier(readConstructor), context, indent, indentNum + 1);
        printEnumAnnotationValue(writer, "getters", accessType, getters, context, indent, indentNum + 1);
        printEnumAnnotationValue(writer, "setters", accessType, setters, context, indent, indentNum + 1);
        printAnnotationValue(writer, "errorMethods", errorMethods, indent, indentNum + 1);
        printAnnotationValue(writer, "serializable", serializable, indent, indentNum + 1);
        printAnnotationValue(writer, "serialVersionUID", serialVersionUID, indent, indentNum + 1);
        printAnnotationValue(writer, "useDefaultBeanProvider", useDefaultBeanProvider, indent, indentNum + 1);
        printEnumAnnotationValue(writer, "configureBeanCacheType", cacheTypeType, configureBeanCacheType, context, indent, indentNum + 1, true);
        writer.print(")");
    }
}
