package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.runtime.annotations.Access;
import io.github.vipcxj.beanknife.runtime.annotations.RemoveViewProperties;
import io.github.vipcxj.beanknife.runtime.annotations.RemoveViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;
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
    private List<String> extraExcludes;
    private Map<String, AnnotationUsage> useAnnotations;
    private List<AnnotationMirror> annotationMirrors;
    private Set<String> annotationNames;

    public static ViewOfData read(@NonNull ProcessingEnvironment environment, @NonNull AnnotationMirror viewOf, @NonNull TypeElement sourceElement) {
        ViewOfData data = new ViewOfData();
        data.load(environment, viewOf, sourceElement);
        return data;
    }

    public void load(@NonNull ProcessingEnvironment environment, @NonNull AnnotationMirror viewOf, @NonNull TypeElement sourceElement) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> annValues = environment.getElementUtils().getElementValuesWithDefaults(viewOf);
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
        this.genName = Utils.getStringAnnotationValue(viewOf, annValues, "genName");
        this.access = getModifier(Utils.getEnumAnnotationValue(viewOf, annValues, "access"));
        this.includes = Utils.getStringArrayAnnotationValue(viewOf, annValues, "includes");
        this.excludes = Utils.getStringArrayAnnotationValue(viewOf, annValues, "excludes");
        this.includePattern = Utils.getStringAnnotationValue(viewOf, annValues, "includePattern");
        this.excludePattern = Utils.getStringAnnotationValue(viewOf, annValues, "excludePattern");
        this.emptyConstructor = getModifier(Utils.getEnumAnnotationValue(viewOf, annValues, "emptyConstructor"));
        this.fieldsConstructor = getModifier(Utils.getEnumAnnotationValue(viewOf, annValues, "fieldsConstructor"));
        this.copyConstructor = getModifier(Utils.getEnumAnnotationValue(viewOf, annValues, "copyConstructor"));
        this.readConstructor = getModifier(Utils.getEnumAnnotationValue(viewOf, annValues, "readConstructor"));
        this.getters = getAccess(Utils.getEnumAnnotationValue(viewOf, annValues, "getters"));
        this.setters = getAccess(Utils.getEnumAnnotationValue(viewOf, annValues, "setters"));
        this.errorMethods = Utils.getBooleanAnnotationValue(viewOf, annValues, "errorMethods");
        this.serializable = Utils.getBooleanAnnotationValue(viewOf, annValues, "serializable");
        this.serialVersionUID = Utils.getLongAnnotationValue(viewOf, annValues, "serialVersionUID");
        this.useDefaultBeanProvider = Utils.getBooleanAnnotationValue(viewOf, annValues, "useDefaultBeanProvider");
        this.configureBeanCacheType = getCacheType(Utils.getEnumAnnotationValue(viewOf, annValues, "configureBeanCacheType"));
        this.extraExcludes = new ArrayList<>();
        List<AnnotationMirror> removeViewProperties = Utils.getAnnotationsOn(environment.getElementUtils(), configElement, RemoveViewProperty.class, RemoveViewProperties.class);
        for (AnnotationMirror removeViewProperty : removeViewProperties) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> values = environment.getElementUtils().getElementValuesWithDefaults(removeViewProperty);
            String exclude = Utils.getStringAnnotationValue(removeViewProperty, values, "value");
            this.extraExcludes.add(exclude);
        }
        this.useAnnotations = AnnotationUsage.collectAnnotationUsages(environment.getElementUtils(), configElement, null);
        collectAnnotations(environment.getElementUtils());
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

    public void reload(ProcessingEnvironment environment) {
        load(environment, this.viewOf, this.sourceElement);
    }

    private static Modifier getModifier(String modifier) {
        if ("io.github.vipcxj.beanknife.runtime.annotations.Access.PUBLIC".equals(modifier)) {
            return Modifier.PUBLIC;
        } else if ("io.github.vipcxj.beanknife.runtime.annotations.Access.PRIVATE".equals(modifier)) {
            return Modifier.PRIVATE;
        } else if ("io.github.vipcxj.beanknife.runtime.annotations.Access.PROTECTED".equals(modifier)) {
            return Modifier.PROTECTED;
        } else if ("io.github.vipcxj.beanknife.runtime.annotations.Access.DEFAULT".equals(modifier)) {
            return Modifier.DEFAULT;
        } else if ("io.github.vipcxj.beanknife.runtime.annotations.Access.NONE".equals(modifier)) {
            return null;
        } else {
            throw new IllegalArgumentException("This is impossible!");
        }
    }

    public static Access getAccess(String qName) {
        if ("io.github.vipcxj.beanknife.runtime.annotations.Access.PUBLIC".equals(qName)) {
            return Access.PUBLIC;
        } else if ("io.github.vipcxj.beanknife.runtime.annotations.Access.PRIVATE".equals(qName)) {
            return Access.PRIVATE;
        } else if ("io.github.vipcxj.beanknife.runtime.annotations.Access.PROTECTED".equals(qName)) {
            return Access.PROTECTED;
        } else if ("io.github.vipcxj.beanknife.runtime.annotations.Access.DEFAULT".equals(qName)) {
            return Access.DEFAULT;
        } else if ("io.github.vipcxj.beanknife.runtime.annotations.Access.NONE".equals(qName)) {
            return Access.NONE;
        } else if ("io.github.vipcxj.beanknife.runtime.annotations.Access.UNKNOWN".equals(qName)) {
            return Access.UNKNOWN;
        } else {
            throw new IllegalArgumentException("This is impossible!");
        }
    }

    public static CacheType getCacheType(String qName) {
        if ("io.github.vipcxj.beanknife.runtime.utils.CacheType.NONE".equals(qName)) {
            return CacheType.NONE;
        } else if ("io.github.vipcxj.beanknife.runtime.utils.CacheType.LOCAL".equals(qName)) {
            return CacheType.LOCAL;
        } else {
            return CacheType.GLOBAL;
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

    public List<String> getExtraExcludes() {
        return extraExcludes;
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
