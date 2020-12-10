package io.github.vipcxj.beanknife.utils;

import io.github.vipcxj.beanknife.annotations.Access;
import io.github.vipcxj.beanknife.annotations.internal.GeneratedMeta;
import io.github.vipcxj.beanknife.annotations.internal.GeneratedView;
import io.github.vipcxj.beanknife.annotations.ViewProperty;
import io.github.vipcxj.beanknife.models.*;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Utils {

    public static String createGetterName(String fieldName, boolean isBoolean) {
        if (fieldName.isEmpty()) {
            throw new IllegalArgumentException("Empty field name is illegal.");
        }
        if (isBoolean && fieldName.length() >= 3 && fieldName.startsWith("is") && Character.isUpperCase(fieldName.charAt(2))) {
            return fieldName;
        }
        if (fieldName.length() == 1) {
            return isBoolean ? "is" + fieldName.toUpperCase() : "get" + fieldName.toUpperCase();
        } else if (Character.isUpperCase(fieldName.charAt(1))){
            return isBoolean ? "is" + fieldName : "get" + fieldName;
        } else {
            String part = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            return isBoolean ? "is" + part : "get" + part;
        }
    }

    public static String createSetterName(String fieldName, boolean isBoolean) {
        if (fieldName.isEmpty()) {
            throw new IllegalArgumentException("Empty field name is illegal.");
        }
        if (isBoolean && fieldName.length() >= 3 && fieldName.startsWith("is") && Character.isUpperCase(fieldName.charAt(2))) {
            return fieldName.replace("is", "set");
        }
        if (fieldName.length() == 1) {
            return "set" + fieldName.toUpperCase();
        } else if (Character.isUpperCase(fieldName.charAt(1))) {
            return "set" + fieldName;
        } else {
            String part = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            return "set" + part;
        }
    }

    public static String extractFieldName(String getterName) {
        boolean startsWithGet = getterName.startsWith("get");
        boolean startsWithIs = getterName.startsWith("is");
        if (startsWithGet || startsWithIs) {
            int preLen = startsWithGet ? 3 : 2;
            String part = getterName.substring(preLen);
            if (part.isEmpty()) {
                return null;
            }
            if (part.length() >= 2 && Character.isUpperCase(part.charAt(0)) && Character.isUpperCase(part.charAt(1))) {
                return part;
            }
            if (part.length() == 1) {
                return part.toLowerCase();
            } else {
                return part.substring(0, 1).toLowerCase() + part.substring(1);
            }
        }
        return null;
    }

    public static String createValidFieldName(String name, Set<String> names) {
        if (SourceVersion.isKeyword(name) || names.contains(name)) {
            return createValidFieldName(name + "_", names);
        } else {
            return name;
        }
    }

    public static Modifier getPropertyModifier(Element e) {
        Set<Modifier> modifiers = e.getModifiers();
        Modifier modifier = Modifier.DEFAULT;
        if (modifiers.contains(Modifier.PUBLIC)) {
            modifier = Modifier.PUBLIC;
        } else if (modifiers.contains(Modifier.PROTECTED)){
            modifier = Modifier.PROTECTED;
        } else if (modifiers.contains(Modifier.PRIVATE)) {
            modifier = Modifier.PRIVATE;
        }
        return modifier;
    }

    private static boolean isWriteable(String setterName, TypeMirror type, List<? extends Element> members, boolean samePackage) {
        boolean writeable = false;
        for (Element member : members) {
            if (member.getKind() == ElementKind.METHOD
                    && !member.getModifiers().contains(Modifier.STATIC)
                    && !member.getModifiers().contains(Modifier.ABSTRACT)
                    && canSeeFromOtherClass(member, samePackage)
                    && setterName.equals(member.getSimpleName().toString())
            ) {
                ExecutableElement getter = (ExecutableElement) member;
                List<? extends VariableElement> parameters = getter.getParameters();
                if (parameters.size() != 1) {
                    continue;
                }
                VariableElement variableElement = parameters.get(0);
                if (!variableElement.asType().equals(type)) {
                    continue;
                }
                writeable = getter.getReturnType().getKind() == TypeKind.VOID;
                break;
            }
        }
        return writeable;
    }

    public static Access resolveGetterAccess(@Nullable ViewOfData viewOf, @Nonnull Access access) {
        Access getter = viewOf != null ? viewOf.getGetters() : Access.UNKNOWN;
        if (access != Access.UNKNOWN) {
            getter = access;
        }
        return getter == Access.UNKNOWN ? Access.PUBLIC : getter;
    }

    public static Access resolveSetterAccess(@Nullable ViewOfData viewOf, @Nonnull Access access) {
        Access setter = viewOf != null ? viewOf.getSetters() : Access.UNKNOWN;
        if (access != Access.UNKNOWN) {
            setter = access;
        }
        return setter == Access.UNKNOWN ? Access.NONE : setter;
    }

    public static Property createPropertyFromBase(
            @Nonnull Context context,
            @Nullable ViewOfData viewOf,
            @Nonnull VariableElement e,
            @Nonnull List<? extends Element> members,
            boolean samePackage
    ) {
        if (e.getKind() != ElementKind.FIELD) {
            return null;
        }
        Modifier modifier = getPropertyModifier(e);
        String name = e.getSimpleName().toString();
        ViewProperty viewProperty = e.getAnnotation(ViewProperty.class);
        Access getter = viewProperty != null ? viewProperty.getter() : Access.UNKNOWN;
        Access setter = viewProperty != null ? viewProperty.setter() : Access.UNKNOWN;
        TypeMirror type = e.asType();
        String setterName = createSetterName(name, type.getKind() == TypeKind.BOOLEAN);
        boolean writeable = isWriteable(setterName, type, members, samePackage);
        return new Property(
                name,
                modifier,
                resolveGetterAccess(viewOf, getter),
                resolveSetterAccess(viewOf, setter),
                Type.extract(type),
                false,
                createGetterName(name, type.getKind() == TypeKind.BOOLEAN),
                setterName,
                writeable,
                e,
                context.getProcessingEnv().getElementUtils().getDocComment(e)
        );
    }

    public static Property createPropertyFromBase(
            @Nonnull Context context,
            @Nullable ViewOfData viewOf,
            @Nonnull ExecutableElement e,
            @Nonnull List<? extends Element> members,
            boolean samePackage
    ) {
        if (e.getKind() != ElementKind.METHOD) {
            return null;
        }
        if (!e.getParameters().isEmpty()) {
            return null;
        }
        TypeMirror type = e.getReturnType();
        if (type.getKind() == TypeKind.VOID) {
            return null;
        }
        String methodName = e.getSimpleName().toString();
        String name = extractFieldName(methodName);
        if (name == null) {
            return null;
        }
        ViewProperty viewProperty = e.getAnnotation(ViewProperty.class);
        String setterName = createSetterName(name, type.getKind() == TypeKind.BOOLEAN);
        boolean writeable = isWriteable(setterName, type, members, samePackage);
        return new Property(
                name,
                getPropertyModifier(e),
                resolveGetterAccess(viewOf, viewProperty != null ? viewProperty.getter() : Access.UNKNOWN),
                resolveSetterAccess(viewOf, viewProperty != null ? viewProperty.setter() : Access.UNKNOWN),
                Type.extract(type),
                true,
                methodName,
                setterName,
                writeable,
                e,
                context.getProcessingEnv().getElementUtils().getDocComment(e)
        );
    }

    public static boolean canSeeFromOtherClass(Modifier modifier, boolean samePackage) {
        if (samePackage) {
            return modifier != Modifier.PRIVATE;
        } else {
            return modifier == Modifier.PUBLIC;
        }
    }

    public static boolean canSeeFromOtherClass(Property property, boolean samePackage) {
        return canSeeFromOtherClass(property.getModifier(), samePackage);
    }

    public static boolean canSeeFromOtherClass(Element element, boolean samePackage) {
        if (samePackage) {
            return !element.getModifiers().contains(Modifier.PRIVATE);
        } else {
            return element.getModifiers().contains(Modifier.PUBLIC);
        }
    }

    public static boolean isNotObjectProperty(Property property) {
        Element parent = property.getElement().getEnclosingElement();
        return parent.getKind() != ElementKind.CLASS || !((TypeElement) parent).getQualifiedName().toString().equals("java.lang.Object");
    }

    @Nonnull
    public static String getAnnotationName(@Nonnull AnnotationMirror mirror) {
        return ((TypeElement) mirror.getAnnotationType().asElement())
                .getQualifiedName().toString();
    }

    @Nonnull
    public static List<AnnotationMirror> extractAnnotations(
            @Nonnull ProcessingEnvironment environment,
            @Nonnull Element element,
            @Nonnull String qName,
            @Nullable String qNames
    ) {
        if (qName.isEmpty()) {
            return Collections.emptyList();
        }
        if (qNames != null && qNames.equals(qName)) {
            throw new IllegalArgumentException("Annotation and Annotations can not be equal: " + qName + ".");
        }
        List<? extends AnnotationMirror> annotationMirrors = element.getAnnotationMirrors();
        List<AnnotationMirror> result = new ArrayList<>();
        for (AnnotationMirror annotationMirror : annotationMirrors) {
            String name = getAnnotationName(annotationMirror);
            if (qName.equals(name)) {
                result.add(annotationMirror);
            }
            if (qNames != null && qNames.equals(name)) {
                Map<? extends ExecutableElement, ? extends AnnotationValue> anValues = environment.getElementUtils().getElementValuesWithDefaults(annotationMirror);
                result.addAll(getAnnotationElement(annotationMirror, anValues));
            }
        }
        return result;
    }

    public static AnnotationValue getAnnotationValue(AnnotationMirror annotation, @Nonnull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @Nonnull String name) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationValues.entrySet()) {
            if (name.equals(entry.getKey().getSimpleName().toString())) {
                return entry.getValue();
            }
        }
        throw new IllegalArgumentException("There is no attribute named \"" + name + "\" in annotation " + getAnnotationName(annotation));
    }

    public enum AnnotationValueKind {
        BOXED, STRING, TYPE, ENUM, ANNOTATION, ARRAY
    }
    private static AnnotationValueKind getAnnotationValueType(AnnotationValue value) {
        Object v = value.getValue();
        if (v instanceof Boolean
                || v instanceof Integer
                || v instanceof Long
                || v instanceof Float
                || v instanceof Short
                || v instanceof Character
                || v instanceof Double
                || v instanceof Byte
        ) {
            return AnnotationValueKind.BOXED;
        } else if (v instanceof String) {
            return AnnotationValueKind.STRING;
        } else if (v instanceof TypeMirror) {
            return AnnotationValueKind.TYPE;
        } else if (v instanceof VariableElement) {
            return AnnotationValueKind.ENUM;
        } else if (v instanceof AnnotationMirror) {
            return AnnotationValueKind.ANNOTATION;
        } else if (v instanceof List) {
            return AnnotationValueKind.ARRAY;
        }
        throw new IllegalArgumentException("This is impossible.");
    }

    private static void throwCastAnnotationValueTypeError(
            @Nonnull AnnotationMirror annotation,
            @Nonnull String attributeName,
            @Nonnull AnnotationValueKind fromKind,
            @Nonnull AnnotationValueKind toKind
    ) {
        throw new IllegalArgumentException(
                "Unable to cast attribute named \""
                        + attributeName
                        + "\" in annotation "
                        + getAnnotationName(annotation)
                        + " from "
                        + fromKind
                        + " to "
                        + toKind
                        + "."
        );
    }



    public static String getStringAnnotationValue(@Nonnull AnnotationMirror annotation, @Nonnull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @Nonnull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.STRING) {
            return (String) annotationValue.getValue();
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.STRING);
        throw new IllegalArgumentException("This is impossible.");
    }

    public static boolean getBooleanAnnotationValue(@Nonnull AnnotationMirror annotation, @Nonnull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @Nonnull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.BOXED) {
            return (Boolean) annotationValue.getValue();
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.BOXED);
        throw new IllegalArgumentException("This is impossible.");
    }

    public static DeclaredType getTypeAnnotationValue(@Nonnull AnnotationMirror annotation, @Nonnull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @Nonnull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.TYPE) {
            return (DeclaredType) annotationValue.getValue();
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.TYPE);
        throw new IllegalArgumentException("This is impossible.");
    }

    public static String getEnumAnnotationValue(@Nonnull AnnotationMirror annotation, @Nonnull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @Nonnull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.ENUM) {
            VariableElement variableElement = (VariableElement) annotationValue.getValue();
            TypeElement enumClass = (TypeElement) variableElement.getEnclosingElement();
            return enumClass.getQualifiedName() + "." + variableElement.getSimpleName();
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.ENUM);
        throw new IllegalArgumentException("This is impossible.");
    }

    public static List<? extends AnnotationValue> getArrayAnnotationValue(@Nonnull AnnotationMirror annotation, @Nonnull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @Nonnull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.ARRAY) {
            //noinspection unchecked
            return (List<? extends AnnotationValue>) annotationValue.getValue();
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.ARRAY);
        throw new IllegalArgumentException("This is impossible.");
    }

    public static String[] getStringArrayAnnotationValue(@Nonnull AnnotationMirror annotation, @Nonnull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @Nonnull String name) {
        List<? extends AnnotationValue> annValues = getArrayAnnotationValue(annotation, annotationValues, name);
        String[] values = new String[annValues.size()];
        int i = 0;
        for (AnnotationValue annValue : annValues) {
            values[i++] = (String) annValue.getValue();
        }
        return values;
    }

    public static DeclaredType[] getTypeArrayAnnotationValue(@Nonnull AnnotationMirror annotation, @Nonnull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @Nonnull String name) {
        List<? extends AnnotationValue> annValues = getArrayAnnotationValue(annotation, annotationValues, name);
        DeclaredType[] values = new DeclaredType[annValues.size()];
        int i = 0;
        for (AnnotationValue annValue : annValues) {
            values[i++] = (DeclaredType) annValue.getValue();
        }
        return values;
    }

    public static List<AnnotationMirror> getAnnotationElement(AnnotationMirror annotation, @Nonnull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues) {
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotationValues.entrySet()) {
            if ("value".equals(entry.getKey().getSimpleName().toString())) {
                //noinspection unchecked
                List<? extends AnnotationValue> values = (List<? extends AnnotationValue>) entry.getValue().getValue();
                List<AnnotationMirror> annotations = new ArrayList<>();
                for (AnnotationValue value : values) {
                    annotations.add((AnnotationMirror) value.getValue());
                }
                return annotations;
            }
        }
        throw new IllegalArgumentException("There is no attribute named value in annotation " + getAnnotationName(annotation));
    }

    public static Type extractGenType(Type baseClassName, String genName, String genPackage, String postfix) {
        if (genName.isEmpty()) {
            if (genPackage.isEmpty()) {
                return baseClassName.appendName(postfix).flatten();
            } else {
                return baseClassName.changePackage(genPackage).appendName(postfix).flatten();
            }
        } else {
            if (genPackage.isEmpty()) {
                return baseClassName.changeSimpleName(genName, true);
            } else {
                return baseClassName.changePackage(genPackage).changeSimpleName(genName, true);
            }
        }
    }

    public static void logWarn(ProcessingEnvironment env, String message) {
        env.getMessager().printMessage(Diagnostic.Kind.WARNING, message);
    }

    public static void logError(ProcessingEnvironment env, String message) {
        env.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
    }

    public static void printIndent(@Nonnull PrintWriter writer, String indent, int num) {
        for (int i = 0; i < num; ++i) {
            writer.print(indent);
        }
    }

    public static void printModifier(@Nonnull PrintWriter writer, @Nonnull Modifier modifier) {
        if (modifier != Modifier.DEFAULT) {
            writer.print(modifier);
            writer.print(" ");
        }
    }

    public static void printAccess(@Nonnull PrintWriter writer, @Nonnull Access access) {
        if (access == Access.UNKNOWN) {
            throw new IllegalArgumentException("Unknown access is not supported.");
        }
        if (access != Access.NONE && access != Access.DEFAULT) {
            writer.print(access);
            writer.print(" ");
        }
    }

    public static void printAnnotationValue(@Nonnull PrintWriter writer, @Nonnull AnnotationValue annValue, @Nonnull Context context, String indent, int indentNum) {
        Object value = annValue.getValue();
        AnnotationValueKind valueType = getAnnotationValueType(annValue);
        switch (valueType) {
            case ENUM: {
                VariableElement enumValue = (VariableElement) value;
                TypeElement enumClass = (TypeElement) enumValue.getEnclosingElement();
                Type enumClassType = Type.extract(enumClass.asType());
                enumClassType.printType(writer, context, false, false);
                writer.print(".");
                writer.print(enumValue.getSimpleName());
                break;
            }
            case BOXED: {
                if (value instanceof Character) {
                    writer.print("'");
                    writer.print(value);
                    writer.print("'");
                } else if (value instanceof Long) {
                    writer.print(value);
                    writer.print("l");
                } else if (value instanceof Float) {
                    writer.print(value);
                    writer.print("f");
                } else {
                    writer.print(value);
                }
                break;
            }
            case TYPE: {
                Type type = Type.extract((TypeMirror) value);
                type.printType(writer, context, false, false);
                writer.print(".class");
                break;
            }
            case STRING: {
                writer.print("\"");
                writer.print(StringEscapeUtils.escapeJava((String) value));
                writer.print("\"");
                break;
            }
            case ANNOTATION: {
                printAnnotation(writer, (AnnotationMirror) value, context, indent, indentNum);
                break;
            }
            case ARRAY: {
                //noinspection unchecked
                List<? extends AnnotationValue> annotationValues = (List<? extends AnnotationValue>) value;
                if (annotationValues.isEmpty()) {
                    writer.print("{}");
                } else {
                    writer.println("{");
                    int i = 0;
                    for (AnnotationValue annotationValue : annotationValues) {
                        Utils.printIndent(writer, indent, indentNum + 1);
                        printAnnotationValue(writer, annotationValue, context, indent, indentNum + 1);
                        if (i != annotationValues.size() - 1) {
                            writer.println(",");
                        } else {
                            writer.println();
                        }
                        ++i;
                    }
                    writer.print("}");
                }
                break;
            }
            default:
                throw new IllegalArgumentException("This is impossible.");
        }
    }

    private static boolean shouldBreakLineForPrintingAnnotation(Collection<? extends AnnotationValue> annotationValues) {
        return annotationValues.size() > 3
                || (annotationValues.size() != 1 && annotationValues.stream().anyMatch(a -> a.getValue() instanceof List && !((List<?>) a.getValue()).isEmpty()));
    }

    public static void printAnnotation(@Nonnull PrintWriter writer, @Nonnull AnnotationMirror annotation, @Nonnull Context context, String indent, int indentNum) {
        writer.print("@");
        Type type = Type.extract(annotation.getAnnotationType());
        type.printType(writer, context, false, false);
        Map<? extends ExecutableElement, ? extends AnnotationValue> attributes = context.getProcessingEnv().getElementUtils().getElementValuesWithDefaults(annotation);
        boolean shouldBreakLine = shouldBreakLineForPrintingAnnotation(attributes.values());
        boolean useValue = attributes.size() == 1 && attributes.keySet().iterator().next().getSimpleName().toString().equals("value");
        if (attributes.isEmpty()) {
            writer.println();
        } else if (useValue) {
            AnnotationValue annotationValue = attributes.values().iterator().next();
            writer.print("(");
            printAnnotationValue(writer, annotationValue, context, indent, indentNum);
            writer.print(")");
        } else if (shouldBreakLine) {
            writer.println("(");
            int i = 0;
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : attributes.entrySet()) {
                Utils.printIndent(writer, indent, indentNum + 1);
                writer.print(entry.getKey().getSimpleName());
                writer.print(" = ");
                printAnnotationValue(writer, entry.getValue(), context, indent, indentNum + 1);
                if (i != attributes.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
                ++i;
            }
            writer.print(")");
        } else {
            writer.print("(");
            int i = 0;
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : attributes.entrySet()) {
                writer.print(entry.getKey().getSimpleName());
                writer.print(" = ");
                printAnnotationValue(writer, entry.getValue(), context, indent, indentNum);
                if (i != attributes.size() - 1) {
                    writer.print(", ");
                }
                ++i;
            }
        }
    }

    public static boolean shouldIgnoredElement(Element element) {
        GeneratedMeta generatedMeta = element.getAnnotation(GeneratedMeta.class);
        if (generatedMeta != null) {
            return true;
        }
        GeneratedView generatedView = element.getAnnotation(GeneratedView.class);
        if (generatedView != null) {
            return true;
        }
        return element.getKind() != ElementKind.CLASS;
    }

    public static boolean isViewMetaTargetTo(ProcessingEnvironment environment, AnnotationMirror viewMeta, TypeElement sourceElement, TypeElement targetElement) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValuesWithDefaults = environment.getElementUtils().getElementValuesWithDefaults(viewMeta);
        for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : elementValuesWithDefaults.entrySet()) {
            if (entry.getKey().getSimpleName().toString().equals("of")) {
                TypeElement target = (TypeElement) ((DeclaredType) entry.getValue().getValue()).asElement();
                if (target.getQualifiedName().toString().equals(Self.class.getCanonicalName())) {
                    target = sourceElement;
                }
                if (target.equals(targetElement)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void writeViewFile(ViewContext context) throws IOException {
        Modifier modifier = context.getViewOf().getAccess();
        if (modifier == null) {
            return;
        }
        JavaFileObject sourceFile = context.getProcessingEnv().getFiler().createSourceFile(context.getGenType().getQualifiedName(), context.getViewOf().getTargetElement(), context.getViewOf().getConfigElement());
        try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
            context.collectData();
            context.print(writer);
        }
    }

    public static void printComment(@Nonnull PrintWriter writer, String comment, String indent, int indentNum) {
        if (comment == null || comment.isEmpty()) {
            return;
        }
        String[] lines = comment.split("[\\r\\n]+");
        if (lines.length == 0) {
            return;
        }
        Utils.printIndent(writer, indent, indentNum);
        writer.println("/**");
        for (String line : lines) {
            Utils.printIndent(writer, indent, indentNum);
            writer.print(" * ");
            writer.println(line);
        }
        Utils.printIndent(writer, indent, indentNum);
        writer.println(" */");
    }
}
