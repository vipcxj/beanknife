package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.AnnotationUtils;
import io.github.vipcxj.beanknife.core.utils.LombokInfo;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.runtime.annotations.Access;
import io.github.vipcxj.beanknife.runtime.utils.AnnotationDest;
import io.github.vipcxj.beanknife.runtime.utils.AnnotationSource;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.util.*;

public class Property {

    private String name;
    private boolean base;
    private final Modifier modifier;
    private Access getter;
    private Access setter;
    private Type type;
    private final boolean method;
    private String getterName;
    private String setterName;
    private boolean writeable;
    private boolean writeMethod;
    private Element element;
    private final String comment;
    private final LombokInfo lombokInfo;
    private Extractor extractor;
    @CheckForNull
    private Type converter;
    private boolean view;
    @CheckForNull
    private Property override;
    @CheckForNull
    private Property flattenOf;
    @CheckForNull
    private Property flattenParent;

    public Property(
            @NonNull String name,
            boolean base,
            Modifier modifier,
            Access getter,
            Access setter,
            Type type,
            boolean method,
            String getterName,
            String setterName,
            @NonNull Element element,
            String comment,
            LombokInfo lombokInfo
    ) {
        this.name = name;
        this.base = base;
        this.modifier = modifier;
        this.getter = getter;
        this.setter = setter;
        this.type = type;
        this.method = method;
        this.getterName = getterName;
        this.setterName = setterName;
        this.element = element;
        this.comment = comment;
        this.override = null;
        this.lombokInfo = lombokInfo;
    }

    public Property(Property other, String commentIfNone) {
        this.name = other.name;
        this.base = other.base;
        this.modifier = other.modifier;
        this.getter = other.getter;
        this.setter = other.setter;
        this.type = other.type;
        this.method = other.method;
        this.getterName = other.getterName;
        this.setterName = other.setterName;
        this.writeable = other.writeable;
        this.writeMethod = other.writeMethod;
        this.element = other.element;
        this.comment = other.comment != null ? other.comment : commentIfNone;
        this.extractor = other.extractor;
        this.converter = other.converter;
        this.view = other.view;
        this.override = other.override;
        this.lombokInfo = other.lombokInfo;
    }

    @NonNull
    public Property extend(
            @CheckForNull Element element,
            @NonNull String newName,
            @CheckForNull Type newType,
            @CheckForNull Access getter,
            @CheckForNull Access setter,
            boolean isView
    ) {
        Property property = new Property(this, null);
        property.view = isView;
        property.base = false;
        property.override = this;
        if (newType != null) {
            property.type = newType;
        }
        if (getter != null) {
            property.getter = getter;
        }
        if (setter != null) {
            property.setter = setter;
        }
        if (element != null) {
            property.element = element;
        }
        if (!Objects.equals(property.name, newName)) {
            property.name = newName;
            property.getterName = Utils.createGetterName(newName, property.type.isBoolean());
            property.setterName = Utils.createSetterName(newName, property.type.isBoolean());
        }
        return property;
    }

    @CheckForNull
    public Property getBase() {
        if (base) {
            return this;
        } else if (override != null) {
            return override.getBase();
        } else {
            return null;
        }
    }

    @CheckForNull
    public Property getField() {
        if (base && !isMethod()) {
            return this;
        } else if (override != null) {
            return override.getField();
        } else {
            return null;
        }
    }

    public Property withWriteInfo(boolean writeable, boolean writeMethod) {
        Property property = new Property(this, null);
        property.writeable = writeable;
        property.writeMethod = writeMethod;
        return property;
    }

    public Property withExtractor(Extractor extractor) {
        Property property = new Property(this, null);
        property.extractor = extractor;
        property.type = extractor.getReturnType();
        return property;
    }

    public Property withConverter(@CheckForNull Type converter) {
        Property property = new Property(this, null);
        property.converter = converter;
        return property;
    }

    public Property overrideBy(@NonNull Property property) {
        Property out = new Property(property, this.comment);
        out.override = this;
        return out;
    }

    public Property flattenOf(@NonNull Property parent, @CheckForNull String newName) {
        Property out = new Property(this, this.comment);
        if (newName != null) {
            out.name = newName;
            out.getterName = Utils.createGetterName(newName, type.isBoolean());
            out.setterName = Utils.createSetterName(newName, type.isBoolean());
        }
        out.flattenOf = this;
        out.flattenParent = parent;
        return out;
    }

    @CheckForNull
    public Property getFlattenOf() {
        return flattenOf;
    }

    @CheckForNull
    public Property getFlattenParent() {
        return flattenParent;
    }

    public TypeMirror getTypeMirror() {
        if (element.getKind() == ElementKind.METHOD) {
            ExecutableElement executableElement = (ExecutableElement) this.element;
            return executableElement.getReturnType();
        } else {
            return element.asType();
        }
    }

    public boolean isLombokReadable(boolean samePackage) {
        return lombokInfo != null && lombokInfo.isReadable(samePackage);
    }

    public boolean isLombokWritable(boolean samePackage) {
        Property field = getField();
        if (field == null) {
            return false;
        }
        boolean isFinal = Objects.requireNonNull(field).getElement().getModifiers().contains(Modifier.FINAL);
        return !isFinal && field.lombokInfo != null && field.lombokInfo.isWritable(samePackage);
    }

    public boolean hasLombokGetter() {
        return lombokInfo != null && lombokInfo.hasGetter();
    }

    public String getName() {
        return name;
    }

    public boolean isBase() {
        return base;
    }

    public Modifier getModifier() {
        return modifier;
    }

    public Access getGetter() {
        return getter;
    }

    public boolean hasGetter() {
        return getter != Access.NONE;
    }

    public boolean sourceCanSeeFrom(@NonNull String fromPackage) {
        boolean samePackage = fromPackage.equals(type.getContext().packageName);
        if (samePackage) {
            return modifier != null && modifier != Modifier.PRIVATE;
        } else {
            return modifier == Modifier.PUBLIC;
        }
    }

    public Access getSetter() {
        return setter;
    }

    public boolean hasSetter() {
        return setter != Access.NONE;
    }

    public boolean setterCanSeeFrom(@NonNull String fromPackage) {
        boolean samePackage = fromPackage.equals(type.getContext().packageName);
        if (samePackage) {
            return setter != Access.NONE && setter != Access.PRIVATE;
        } else {
            return setter == Access.PUBLIC;
        }
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

    public String getSetterName() {
        return setterName;
    }

    public boolean isWriteable() {
        return writeable;
    }

    public boolean isWriteMethod() {
        return writeMethod;
    }

    public Element getElement() {
        return element;
    }

    public String getComment() {
        return comment;
    }

    @CheckForNull
    public Extractor getExtractor() {
        return extractor;
    }

    @CheckForNull
    public Type getConverter() {
        return converter;
    }

    public boolean isView() {
        return view;
    }

    @CheckForNull
    public Property getOverride() {
        return override;
    }

    public boolean isDynamic() {
        return extractor != null && extractor.isDynamic();
    }

    public boolean isCustomMethod() {
        return extractor instanceof StaticMethodExtractor;
    }

    public List<? extends AnnotationMirror> getAnnotations() {
        return element.getAnnotationMirrors();
    }

    public AnnotationInfo getAnnotation(Elements elements, String annotationName) {
        AnnotationMirror an = getAnnotations().stream().filter(a -> Utils.isThisAnnotation(a, annotationName)).findAny().orElse(null);
        if (an == null) {
            return null;
        }
        return new AnnotationInfo(elements, an);
    }

    public <A extends Annotation> AnnotationInfo getAnnotation(Elements elements, Class<A> annotationType) {
        AnnotationMirror an = getAnnotations().stream().filter(a -> Utils.isThisAnnotation(a, annotationType)).findAny().orElse(null);
        if (an == null) {
            return null;
        }
        return new AnnotationInfo(elements, an);
    }

    @NonNull
    public Map<String, AnnotationUsage> collectAnnotationUsages(@NonNull ViewContext context) {
        Map<String, AnnotationUsage> baseUsages = context.getViewOf().getUseAnnotations();
        if (!base) {
            Property base = getBase();
            if (base != null) {
                baseUsages = base.collectAnnotationUsages(context);
            }
        }
        return AnnotationUsage.collectAnnotationUsages(context.getProcessingEnv().getElementUtils(), element, baseUsages);
    }

    private AnnotationSource getAnnotationSource() {
        if (!base) {
            return AnnotationSource.CONFIG;
        } else if (isMethod()) {
            return AnnotationSource.TARGET_GETTER;
        } else {
            return AnnotationSource.TARGET_FIELD;
        }
    }

    private boolean shouldAddAnnotation(@NonNull Elements elements, @NonNull Map<String, AnnotationUsage> useAnnotations, @NonNull AnnotationMirror annotationMirror, @NonNull AnnotationDest pos, boolean test) {
        boolean add = false;
        if (test) {
            Set<AnnotationDest> dest = AnnotationUsage.getAnnotationDest(useAnnotations, annotationMirror, getAnnotationSource());
            AnnotationDest propertyPos = element.getKind() == ElementKind.FIELD ? AnnotationDest.FIELD : AnnotationDest.GETTER;
            if (dest != null && (dest.contains(pos) || (dest.contains(AnnotationDest.SAME) && propertyPos == pos))) {
                ElementType elementType = pos.toElementType();
                add = Utils.annotationCanPutOn(elements, annotationMirror, elementType);
            }
        } else {
            add = true;
        }
        return add;
    }

    private void addAnnotation(@NonNull Elements elements, @NonNull Map<String, AnnotationUsage> useAnnotations, List<AnnotationMirror> annotationMirrors, Set<String> annotationNames, AnnotationMirror annotationMirror, @NonNull AnnotationDest pos, boolean test) {
        List<AnnotationMirror> annotationComponents = Utils.getRepeatableAnnotationComponents(elements, annotationMirror);
        if (annotationComponents == null) {
            if (shouldAddAnnotation(elements, useAnnotations, annotationMirror, pos, test)) {
                String annotationName = AnnotationUtils.getAnnotationName(annotationMirror);
                if (!Utils.isAnnotationRepeatable(elements, annotationMirror)) {
                    annotationMirrors.removeIf(a -> AnnotationUtils.getAnnotationName(a).equals(annotationName));
                }
                annotationMirrors.add(annotationMirror);
                annotationNames.add(annotationName);
            }
        } else {
            for (AnnotationMirror annotationComponent : annotationComponents) {
                if (shouldAddAnnotation(elements, useAnnotations, annotationMirror, pos, test)) {
                    String annotationName = AnnotationUtils.getAnnotationName(annotationComponent);
                    annotationMirrors.add(annotationComponent);
                    annotationNames.add(annotationName);
                }
            }
        }
    }

    public List<AnnotationMirror> collectAnnotations(@NonNull ViewContext context, @NonNull AnnotationDest pos) {
        return collectAnnotations(context, pos, null);
    }

    private List<AnnotationMirror> collectAnnotations(@NonNull ViewContext context, @NonNull AnnotationDest pos, @CheckForNull Map<String, AnnotationUsage> annotationUsages) {
        if (pos == AnnotationDest.SAME) {
            throw new IllegalArgumentException("The annotation pos should not be SAME here.");
        }
        if (pos == AnnotationDest.TYPE) {
            throw new IllegalArgumentException("The annotation pos should not be TYPE here.");
        }
        Elements elements = context.getProcessingEnv().getElementUtils();
        annotationUsages = annotationUsages == null ? collectAnnotationUsages(context) : annotationUsages;
        List<AnnotationMirror> annotationMirrors = new ArrayList<>();
        Set<String> annotationNames = new HashSet<>();
        if (!base) {
            Property baseProperty = getBase();
            if (baseProperty != null) {
                for (AnnotationMirror annotationMirror : baseProperty.collectAnnotations(context, pos, annotationUsages)) {
                    addAnnotation(elements, annotationUsages, annotationMirrors, annotationNames, annotationMirror, pos, false);
                }
            }
        } else if (isMethod()) {
            Property fieldProperty = getField();
            if (fieldProperty != null) {
                for (AnnotationMirror annotationMirror : fieldProperty.collectAnnotations(context, pos, annotationUsages)) {
                    addAnnotation(elements, annotationUsages, annotationMirrors, annotationNames, annotationMirror, pos, false);
                }
            }
        }
        for (AnnotationMirror annotation : getAnnotations()) {
            addAnnotation(elements, annotationUsages, annotationMirrors, annotationNames, annotation, pos, true);
        }
        return annotationMirrors;
    }

    public void printAnnotations(@NonNull PrintWriter writer, @NonNull ViewContext context, String indent, int identNum) {
        for (AnnotationMirror annotationMirror : collectAnnotations(context, AnnotationDest.FIELD)) {
            Utils.printIndent(writer, indent, identNum);
            AnnotationUtils.printAnnotation(writer, annotationMirror, context, indent, identNum);
            writer.println();
        }
    }

    public String getOriginalValueString(@NonNull String sourceVar) {
        Property base = getBase();
        if (base == null) {
            throw new IllegalArgumentException("This is impossible!");
        }
        if (isMethod() || hasLombokGetter()) {
            return sourceVar + "." + base.getGetterName() + "()";
        } else {
            return sourceVar + "." + base.getName();
        }
    }

    public void printType(@NonNull PrintWriter writer, @NonNull Context context, boolean generic, boolean withBound) {
        type.printType(writer, context, generic, withBound);
    }

    public void printField(@NonNull PrintWriter writer, @NonNull Context context, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        writer.print("private ");
        printType(writer, context, true, false);
        writer.print(" ");
        writer.print(context.getMappedFieldName(this));
        writer.println(";");
    }

    public void printGetter(@NonNull PrintWriter writer, @NonNull Context context, String indent, int indentNum) {
        if (!hasGetter()) return;
        Property flattenParent = getFlattenParent();
        if (flattenParent != null && flattenParent.isDynamic() && !context.hasExtraField(flattenParent.getName())) {
            Utils.printIndent(writer, indent, indentNum);
            writer.print("private transient ");
            flattenParent.printType(writer, context, true, false);
            writer.print(" ");
            writer.print(context.calcExtraField(flattenParent.getName()));
            writer.println(";");
            writer.println();
        }
        Utils.printComment(writer, comment, true, indent, indentNum);
        Utils.printIndent(writer, indent, indentNum);
        Utils.printAccess(writer, getter);
        printType(writer, context, true, false);
        writer.print(" ");
        writer.print(getGetterName());
        writer.println("() {");
        if (flattenParent != null && flattenParent.isDynamic()) {
            String cachedFieldName = context.calcExtraField(flattenParent.getName());
            Utils.printIndent(writer, indent, indentNum + 1);
            writer.print("if (this.");
            writer.print(cachedFieldName);
            writer.println(" == null) {");
            Utils.printIndent(writer, indent, indentNum + 1);
            writer.print("this.");
            writer.print(cachedFieldName);
            writer.print(" = ");
            assert flattenParent.getExtractor() != null;
            ((DynamicMethodExtractor) flattenParent.getExtractor()).print(writer);
            writer.println(";");
            Utils.printIndent(writer, indent, indentNum + 1);
            writer.println("}");
        }
        Utils.printIndent(writer, indent, indentNum + 1);
        writer.print("return ");
        if (flattenParent != null && flattenParent.isDynamic()) {
            Property flattenOf = getFlattenOf();
            assert flattenOf != null;
            writer.print("this.");
            writer.print(context.calcExtraField(flattenParent.getName()));
            writer.print(".");
            writer.print(flattenOf.getGetterName());
            writer.print("()");
        } else if (flattenParent == null && isDynamic()) {
            ((DynamicMethodExtractor) extractor).print(writer);
        } else {
            writer.print("this.");
            writer.print(context.getMappedFieldName(this));
        }
        writer.println(";");
        Utils.printIndent(writer, indent, indentNum);
        writer.println("}");
    }

    public void printSetter(@NonNull PrintWriter writer, @NonNull Context context, String indent, int indentNum) {
        if (!hasSetter()) return;
        Utils.printIndent(writer, indent, indentNum);
        Utils.printAccess(writer, setter);
        writer.print("void ");
        writer.print(getSetterName());
        writer.print("(");
        printType(writer, context, true, false);
        writer.print(" ");
        String mappedFieldName = context.getMappedFieldName(this);
        writer.print(mappedFieldName);
        writer.println(") {");
        Utils.printIndent(writer, indent, indentNum + 1);
        writer.print("this.");
        writer.print(mappedFieldName);
        writer.print(" = ");
        writer.print(mappedFieldName);
        writer.println(";");
        Utils.printIndent(writer, indent, indentNum);
        writer.println("}");
    }

    @Override
    public String toString() {
        return "Property{" +
                "name='" + name + '\'' +
                ", modifier=" + modifier +
                ", getter=" + getter +
                ", setter=" + setter +
                ", type=" + type +
                ", method=" + method +
                ", getterName='" + getterName + '\'' +
                ", setterName='" + setterName + '\'' +
                ", writeable=" + writeable +
                ", element=" + element +
                ", comment='" + comment + '\'' +
                ", extractor=" + extractor +
                ", converter=" + converter +
                ", view=" + view +
                ", override=" + override +
                '}';
    }
}
