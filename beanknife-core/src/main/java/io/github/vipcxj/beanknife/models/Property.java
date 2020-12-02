package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.Nonnull;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import java.io.PrintWriter;

public class Property {

    private final String name;
    private final Modifier modifier;
    private final Type type;
    private final boolean method;
    private final String getterName;
    private final String setterName;
    private final boolean writeable;
    private final Element element;
    private final String comment;

    public Property(
            String name,
            Modifier modifier,
            Type type,
            boolean method,
            String getterName,
            String setterName,
            boolean writeable,
            Element element,
            String comment
    ) {
        this.name = name;
        this.modifier = modifier;
        this.type = type;
        this.method = method;
        this.getterName = getterName;
        this.setterName = setterName;
        this.writeable = writeable;
        this.element = element;
        this.comment = comment;
    }

    public Property(Property other, String commentIfNone) {
        this.name = other.name;
        this.modifier = other.modifier;
        this.type = other.type;
        this.method = other.method;
        this.getterName = other.getterName;
        this.setterName = other.setterName;
        this.writeable = other.writeable;
        this.element = other.element;
        this.comment = other.comment != null ? other.comment : commentIfNone;
    }

    public String getName() {
        return name;
    }

    public Modifier getModifier() {
        return modifier;
    }

    public Type getType() {
        return type;
    }

    public boolean isMethod() {
        return method;
    }

    public String getGetterName() {
        return getterName;
    }

    public Element getElement() {
        return element;
    }

    public String getComment() {
        return comment;
    }

    public void printType(@Nonnull PrintWriter writer, @Nonnull Context context, boolean generic, boolean withBound) {
        type.printType(writer, context, generic, withBound);
    }

    public void printField(@Nonnull PrintWriter writer, @Nonnull Context context, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        writer.print("private ");
        printType(writer, context, true, false);
        writer.print(" ");
        writer.print(context.getMappedFieldName(this));
        writer.println(";");
    }

    public void printGetter(@Nonnull PrintWriter writer, @Nonnull Context context, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        Utils.printModifier(writer, modifier);
        printType(writer, context, true, false);
        writer.print(" ");
        writer.print(getGetterName());
        writer.println("() {");
        Utils.printIndent(writer, indent, indentNum);
        writer.print(indent);
        writer.print("return this.");
        writer.print(context.getMappedFieldName(this));
        writer.println(";");
        Utils.printIndent(writer, indent, indentNum);
        writer.println("}");
    }

    @Override
    public String toString() {
        return "Property{" +
                "name='" + name + '\'' +
                ", modifier=" + modifier +
                ", type=" + type +
                ", method=" + method +
                ", getterName='" + getterName + '\'' +
                ", element=" + element +
                ", comment='" + comment + '\'' +
                '}';
    }
}
