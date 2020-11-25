package io.github.vipcxj.beanknife;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class Property {

    private final String name;
    private final Modifier modifier;
    private final TypeMirror type;
    private final boolean method;
    private final String methodName;
    private final Element owner;

    public Property(String name, Modifier modifier, TypeMirror type, boolean method, String methodName, Element owner) {
        this.name = name;
        this.modifier = modifier;
        this.type = type;
        this.method = method;
        this.methodName = methodName;
        this.owner = owner;
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

    public Element getOwner() {
        return owner;
    }

    @Override
    public String toString() {
        return "Property{" +
                "name='" + name + '\'' +
                ", modifier=" + modifier +
                ", type=" + type +
                ", method=" + method +
                ", methodName='" + methodName + '\'' +
                ", owner=" + owner +
                '}';
    }
}
