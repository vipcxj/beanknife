package io.github.vipcxj.beanknife;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class PropertyElementHelper {

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

    public static Property createProperty(VariableElement e) {
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
                e.getEnclosingElement());
    }

    public static Property createProperty(ExecutableElement e) {
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
        return new Property(name, getPropertyModifier(e), type, true, methodName, e.getEnclosingElement());
    }

    public static void addProperty(List<Property> properties, Property property, boolean override) {
        ListIterator<Property> iterator = properties.listIterator();
        boolean add = false;
        while (iterator.hasNext()) {
            Property p = iterator.next();
            if (p.getMethodName().equals(property.getMethodName())) {
                if (override || p.isMethod() == property.isMethod() || property.isMethod()) {
                    iterator.remove();
                    iterator.add(property);
                    add = true;
                } else {
                    return;
                }
            }
        }
        if (!add) {
            properties.add(property);
        }
    }
}
