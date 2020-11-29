package io.github.vipcxj.beanknife.utils;

import io.github.vipcxj.beanknife.models.Property;
import io.github.vipcxj.beanknife.models.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
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
        Modifier modifier = null;
        if (modifiers.contains(Modifier.PUBLIC)) {
            modifier = Modifier.PUBLIC;
        } else if (modifiers.contains(Modifier.PROTECTED)){
            modifier = Modifier.PROTECTED;
        } else if (modifiers.contains(Modifier.PRIVATE)) {
            modifier = Modifier.PRIVATE;
        }
        return modifier;
    }

    public static Property createProperty(ProcessingEnvironment environment, VariableElement e) {
        if (e.getKind() != ElementKind.FIELD) {
            return null;
        }
        String name = e.getSimpleName().toString();
        TypeMirror type = e.asType();
        return new Property(
                name,
                getPropertyModifier(e),
                type,
                false,
                createGetterName(name, type.getKind() == TypeKind.BOOLEAN),
                e,
                environment.getElementUtils().getDocComment(e)
        );
    }

    public static Property createProperty(ProcessingEnvironment environment, ExecutableElement e) {
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
        return new Property(
                name,
                getPropertyModifier(e),
                type,
                true,
                methodName,
                e,
                environment.getElementUtils().getDocComment(e)
        );
    }

    public static boolean canSeeFromOtherClass(Property property, boolean samePackage) {
        if (samePackage) {
            return property.getModifier() != Modifier.PRIVATE;
        } else {
            return property.getModifier() == Modifier.PUBLIC;
        }
    }

    public static boolean canSeeFromOtherClass(Element element, boolean samePackage) {
        if (samePackage) {
            return !element.getModifiers().contains(Modifier.PRIVATE);
        } else {
            return element.getModifiers().contains(Modifier.PUBLIC);
        }
    }

    private static boolean isNotObjectProperty(Property property) {
        Element parent = property.getElement().getEnclosingElement();
        return parent.getKind() != ElementKind.CLASS || !((TypeElement) parent).getQualifiedName().toString().equals("java.lang.Object");
    }

    public static void addProperty(ProcessingEnvironment processingEnv, List<Property> properties, Property property, boolean samePackage, boolean override) {
        Elements elementUtils = processingEnv.getElementUtils();
        boolean done = false;
        ListIterator<Property> iterator = properties.listIterator();
        while (iterator.hasNext()) {
            Property p = iterator.next();
            if (elementUtils.hides(property.getElement(), p.getElement())) {
                iterator.remove();
                if (canSeeFromOtherClass(property, samePackage) && isNotObjectProperty(property)) {
                    iterator.add(new Property(property, p.getComment()));
                }
                done = true;
                break;
            } else if (elementUtils.hides(p.getElement(), property.getElement())) {
                done = true;
                break;
            } else if (p.getMethodName().equals(property.getMethodName())) {
                if (override || (!p.isMethod() && property.isMethod())) {
                    iterator.remove();
                    if (canSeeFromOtherClass(property, samePackage) && isNotObjectProperty(property)) {
                        iterator.add(new Property(property, p.getComment()));
                    }
                } else if (p.isMethod() == property.isMethod()) {
                    Element ownerP = p.getElement().getEnclosingElement();
                    Element ownerProperty = property.getElement().getEnclosingElement();
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "Property conflict: "
                                    + (ownerP != null
                                    ? p.getElement().getSimpleName() + " in " + ownerP.getSimpleName()
                                    : p.getElement().getSimpleName())
                                    + " / "
                                    + (ownerProperty != null
                                    ? property.getElement().getSimpleName() + " in " + ownerProperty.getSimpleName()
                                    : property.getElement().getSimpleName())
                    );
                }
                done = true;
                break;
            }
        }
        if (!done && canSeeFromOtherClass(property, samePackage) && isNotObjectProperty(property)) {
            properties.add(property);
        }
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

    public static DeclaredType getTypeAnnotationValue(@Nonnull AnnotationMirror annotation, @Nonnull Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues, @Nonnull String name) {
        AnnotationValue annotationValue = getAnnotationValue(annotation, annotationValues, name);
        AnnotationValueKind kind = getAnnotationValueType(annotationValue);
        if (kind == AnnotationValueKind.TYPE) {
            return (DeclaredType) annotationValue.getValue();
        }
        throwCastAnnotationValueTypeError(annotation, name, kind, AnnotationValueKind.TYPE);
        throw new IllegalArgumentException("This is impossible.");
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

    public static Type extractClassName(ProcessingEnvironment environment, AnnotationMirror annotation, Type baseClassName, String simpleClassNameVar, String packageNameVar, String postfix) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues = environment.getElementUtils().getElementValuesWithDefaults(annotation);
        String simpleClassName = Utils.getStringAnnotationValue(annotation, annotationValues, simpleClassNameVar);
        String packageName = Utils.getStringAnnotationValue(annotation, annotationValues, packageNameVar);
        if (simpleClassName.isEmpty()) {
            if (packageName.isEmpty()) {
                return baseClassName.appendName(postfix);
            } else {
                return baseClassName.changePackage(packageName).appendName(postfix);
            }
        } else {
            if (packageName.isEmpty()) {
                return baseClassName.changeSimpleName(simpleClassName);
            } else {
                return baseClassName.changePackage(packageName).changeSimpleName(simpleClassName);
            }
        }
    }

    public static List<Property> collectProperties(ProcessingEnvironment environment, TypeElement element, boolean samePackage) {
        List<Property> properties = new LinkedList<>();
        Elements elementUtils = environment.getElementUtils();
        List<? extends Element> members = elementUtils.getAllMembers(element);
        for (Element member : members) {
            Property property = null;
            if (member.getKind() == ElementKind.FIELD) {
                property = Utils.createProperty(environment, (VariableElement) member);
            } else if (member.getKind() == ElementKind.METHOD) {
                property = Utils.createProperty(environment, (ExecutableElement) member);
            }
            if (property != null) {
                Utils.addProperty(environment, properties, property, samePackage, false);
            }
        }
        return properties;
    }

    public static void logError(ProcessingEnvironment env, String message) {
        env.getMessager().printMessage(Diagnostic.Kind.ERROR, message);
    }
}
