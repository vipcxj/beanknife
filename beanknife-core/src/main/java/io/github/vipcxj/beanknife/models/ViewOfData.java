package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.annotations.Access;
import io.github.vipcxj.beanknife.annotations.ViewOf;
import io.github.vipcxj.beanknife.utils.Self;
import io.github.vipcxj.beanknife.utils.Utils;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class ViewOfData {
    private TypeElement targetElement;
    private TypeElement configElement;
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

    public static ViewOfData read(@Nonnull ProcessingEnvironment environment, @Nonnull AnnotationMirror viewOf, @Nonnull TypeElement sourceElement) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> annValues = environment.getElementUtils().getElementValuesWithDefaults(viewOf);
        ViewOfData data = new ViewOfData();
        DeclaredType value = Utils.getTypeAnnotationValue(viewOf, annValues, "value");
        data.targetElement = (TypeElement) value.asElement();
        if (Self.class.getCanonicalName().equals(data.targetElement.getQualifiedName().toString())) {
            data.targetElement = sourceElement;
        }
        DeclaredType config = Utils.getTypeAnnotationValue(viewOf, annValues, "config");
        data.configElement = (TypeElement) config.asElement();
        if (Self.class.getCanonicalName().equals(data.configElement.getQualifiedName().toString())) {
            data.configElement = sourceElement;
        }
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
        } else if ("io.github.vipcxj.beanknife.annotations.Access.PROTECTED".equals(modifier)) {
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

    private static void printStringAnnotationValue(PrintWriter writer, String name, String value, String indent, int indentNum) {
        if (!value.isEmpty()) {
            Utils.printIndent(writer, indent, indentNum);
            writer.print(name);
            writer.print(" = \"");
            writer.print(StringEscapeUtils.escapeJava(value));
            writer.println("\",");
        }
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

    public static void printAccessAnnotationValue(PrintWriter writer, String name, Type accessType, Access access, Context context, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        writer.print(name);
        writer.print(" = ");
        accessType.printType(writer, context, false, false);
        writer.print(".");
        writer.print(access.name());
        writer.println(",");
    }

    public void print(PrintWriter writer, Context context, String indent, int indentNum) {
        writer.print("@");
        Type viewOfType = Type.extract(context.getProcessingEnv(), ViewOf.class);
        viewOfType.printType(writer, context, false, false);
        TypeElement access = context.getProcessingEnv().getElementUtils().getTypeElement(Access.class.getCanonicalName());
        Type accessType = Type.extract(access.asType());
        writer.println("(");
        Utils.printIndent(writer, indent, indentNum + 1);
        writer.print("value = ");
        Type.extract(targetElement.asType()).printType(writer, context, false, false);
        writer.println(".class,");
        Utils.printIndent(writer, indent, indentNum + 1);
        writer.print("config = ");
        Type.extract(configElement.asType()).printType(writer, context, false, false);
        writer.println(".class,");
        printStringAnnotationValue(writer, "genPackage", genPackage, indent, indentNum + 1);
        printStringAnnotationValue(writer, "genName", genName, indent, indentNum + 1);
        printAccessAnnotationValue(writer, "access", accessType, Access.fromModifier(this.access), context, indent, indentNum + 1);
        printStringArrayAnnotationValue(writer, "includes", includes, indent, indentNum + 1);
        printStringArrayAnnotationValue(writer, "excludes", excludes, indent, indentNum + 1);
        printStringAnnotationValue(writer, "includePattern", includePattern, indent, indentNum + 1);
        printStringAnnotationValue(writer, "excludePattern", excludePattern, indent, indentNum + 1);
        printAccessAnnotationValue(writer, "emptyConstructor", accessType, Access.fromModifier(emptyConstructor), context, indent, indentNum + 1);
        printAccessAnnotationValue(writer, "fieldsConstructor", accessType, Access.fromModifier(fieldsConstructor), context, indent, indentNum + 1);
        printAccessAnnotationValue(writer, "copyConstructor", accessType, Access.fromModifier(copyConstructor), context, indent, indentNum + 1);
        printAccessAnnotationValue(writer, "readMethod", accessType, Access.fromModifier(readMethod), context, indent, indentNum + 1);
        printAccessAnnotationValue(writer, "getters", accessType, getters, context, indent, indentNum + 1);
        printAccessAnnotationValue(writer, "setters", accessType, setters, context, indent, indentNum + 1);
        Utils.printIndent(writer, indent, indentNum + 1);
        writer.print("errorMethods = ");
        writer.println(errorMethods);
        writer.print(")");
    }
}
