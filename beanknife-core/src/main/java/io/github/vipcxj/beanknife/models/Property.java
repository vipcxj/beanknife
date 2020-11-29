package io.github.vipcxj.beanknife.models;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

public class Property {

    private final String name;
    private final Modifier modifier;
    private final TypeMirror type;
    private final boolean method;
    private final String methodName;
    private final Element element;
    private final String comment;

    public Property(String name, Modifier modifier, TypeMirror type, boolean method, String methodName, Element element, String comment) {
        this.name = name;
        this.modifier = modifier;
        this.type = type;
        this.method = method;
        this.methodName = methodName;
        this.element = element;
        this.comment = comment;
    }

    public Property(Property other, String commentIfNone) {
        this.name = other.name;
        this.modifier = other.modifier;
        this.type = other.type;
        this.method = other.method;
        this.methodName = other.methodName;
        this.element = other.element;
        this.comment = other.comment != null ? other.comment : commentIfNone;
    }

    public String getName() {
        return name;
    }

    public Modifier getModifier() {
        return modifier;
    }

    public TypeMirror getType() {
        return type;
    }

    public boolean isMethod() {
        return method;
    }

    public String getMethodName() {
        return methodName;
    }

    public Element getElement() {
        return element;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return "Property{" +
                "name='" + name + '\'' +
                ", modifier=" + modifier +
                ", type=" + type +
                ", method=" + method +
                ", methodName='" + methodName + '\'' +
                ", element=" + element +
                ", comment='" + comment + '\'' +
                '}';
    }
}
