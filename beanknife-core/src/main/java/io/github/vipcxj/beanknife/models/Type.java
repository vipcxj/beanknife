package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Type {
    private final String packageName;
    private final String simpleName;
    private final boolean array;
    private final boolean annotation;
    private final boolean typeVar;
    private final boolean wildcard;
    private final List<Type> parameters;
    private final Type container;
    private final List<Type> upperBounds;
    private final List<Type> lowerBounds;

    /**
     * 构造函数
     * @param packageName 包名
     * @param simpleName 类名，不包括包名，特别的，对于嵌套类，有如下形式Parent.Nest1.Nest2
     * @param array 是否是数组
     * @param parameters 泛型参数列表
     * @param upperBounds 类型上界，可为空，此时默认为 {@link Object}
     * @param container 包裹类，可为空
     * @param lowerBounds 类型下界，可为空
     */
    public Type(
            @Nonnull String packageName,
            @Nonnull String simpleName,
            boolean array,
            boolean annotation,
            boolean typeVar,
            boolean wildcard,
            @Nonnull List<Type> parameters,
            @Nullable Type container,
            @Nonnull List<Type> upperBounds,
            @Nonnull List<Type> lowerBounds
    ) {
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.array = array;
        this.annotation = annotation;
        this.typeVar = typeVar;
        this.wildcard = wildcard;
        this.parameters = parameters;
        this.container = container;
        this.upperBounds = upperBounds;
        this.lowerBounds = lowerBounds;
    }

    public Type toArrayType() {
        if (array) return this;
        if (wildcard) {
            throw new UnsupportedOperationException();
        }
        return new Type(
                packageName,
                simpleName,
                true,
                annotation,
                typeVar,
                false,
                parameters,
                container,
                upperBounds,
                lowerBounds
        );
    }

    public Type changeSimpleName(String simpleName, boolean removeParents) {
        if (wildcard) {
            throw new UnsupportedOperationException();
        }
        return new Type(
                packageName,
                simpleName,
                array,
                annotation,
                typeVar,
                false,
                parameters,
                !removeParents ? container : null,
                upperBounds,
                lowerBounds
        );
    }

    public Type changePackage(String packageName) {
        return new Type(
                packageName,
                simpleName,
                array,
                annotation,
                typeVar,
                wildcard,
                parameters,
                container,
                upperBounds,
                lowerBounds
        );
    }

    public Type appendName(String postfix) {
        return new Type(
                packageName,
                simpleName + postfix,
                array,
                annotation,
                typeVar,
                wildcard,
                parameters,
                container,
                upperBounds,
                lowerBounds
        );
    }

    public Type flatten() {
        if (container == null) {
            return this;
        }
        return new Type(
                packageName,
                getEnclosedFlatSimpleName(),
                array,
                annotation,
                typeVar,
                wildcard,
                parameters,
                null,
                upperBounds,
                lowerBounds
        );
    }

    public Type withoutParameters() {
        if (parameters.isEmpty()) {
            return this;
        }
        return new Type(
                packageName,
                simpleName,
                array,
                annotation,
                typeVar,
                wildcard,
                Collections.emptyList(),
                container,
                upperBounds,
                lowerBounds
        );
    }

    public Type withParameters(List<Type> parameters) {
        return new Type(
                packageName,
                simpleName,
                array,
                annotation,
                typeVar,
                wildcard,
                parameters,
                container,
                upperBounds,
                lowerBounds
        );
    }

    public String relativeName(Type type, boolean imported) {
        String packageName = type.getPackageName();
        String qualifiedName = type.getQualifiedName();
        String packageNameOfMe = getPackageName();
        String qualifiedNameOfMe = getQualifiedName();
        if (packageName.equals(packageNameOfMe) && !isPackage() && type.isNested()) {
            if (qualifiedName.equals(qualifiedNameOfMe)) {
                return type.getSimpleName();
            }
            if (qualifiedName.startsWith(qualifiedNameOfMe) && qualifiedName.charAt(qualifiedNameOfMe.length()) == '.') {
                return type.getSimpleName();
            }
            if (qualifiedNameOfMe.startsWith(qualifiedName) && qualifiedNameOfMe.charAt(qualifiedName.length()) == '.') {
                return type.getSimpleName();
            }
        }
        return (imported || type.isLangType()) ? type.getEnclosedSimpleName() : type.getQualifiedName();
    }

    public static Type create(String packageName, String typeName, boolean array, boolean annotation) {
        return new Type(
                packageName,
                typeName,
                array,
                annotation,
                false,
                false,
                Collections.emptyList(),
                null,
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    public static Type extract(ProcessingEnvironment env, Class<?> clazz) {
        return extract(env.getElementUtils().getTypeElement(clazz.getCanonicalName()).asType());
    }

    private static List<Type> parseBounds(TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.INTERSECTION) {
            IntersectionType intersectionType = (IntersectionType) typeMirror;
            return intersectionType.getBounds()
                    .stream()
                    .map(Type::extract)
                    .filter(Type::isNotObjectType)
                    .collect(Collectors.toList());
        } else {
            return Collections.singletonList(Type.extract(typeMirror));
        }
    }

    @Nonnull
    public static Type createUnboundedWildcard() {
        return new Type(
                "",
                "?",
                false,
                false,
                false,
                true,
                Collections.emptyList(),
                null,
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    @Nonnull
    public static Type createExtendsWildcard(@Nonnull List<Type> upperBounds) {
        return new Type(
                "",
                "?",
                false,
                false,
                false,
                true,
                Collections.emptyList(),
                null,
                upperBounds,
                Collections.emptyList()
        );
    }

    @Nonnull
    public static Type createSuperWildcard(@Nonnull List<Type> lowerBounds) {
        return new Type(
                "",
                "?",
                false,
                false,
                false,
                true,
                Collections.emptyList(),
                null,
                Collections.emptyList(),
                lowerBounds
        );
    }

    public static Type createTypeParameter(@Nonnull String name, @Nonnull List<Type> bounds) {
        return new Type(
                "",
                name,
                false,
                false,
                true,
                false,
                Collections.emptyList(),
                null,
                bounds,
                Collections.emptyList()
        );
    }

    @Nonnull
    public static Type extract(TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return new Type(
                    "",
                    type.toString(),
                    false,
                    false,
                    false,
                    false,
                    Collections.emptyList(),
                    null,
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        } else if (type.getKind() == TypeKind.ARRAY) {
            ArrayType arrayType = (ArrayType) type;
            return extract(arrayType.getComponentType()).toArrayType();
        } else if (type.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) type;
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            List<Type> parameters = new ArrayList<>();
            for (TypeMirror typeArgument : typeArguments) {
                parameters.add(extract(typeArgument));
            }
            TypeElement element = (TypeElement) declaredType.asElement();
            boolean annotation = element.getKind() == ElementKind.ANNOTATION_TYPE;
            Element enclosingElement = element.getEnclosingElement();
            Type parentType = null;
            String packageName;
            if (enclosingElement.getKind() == ElementKind.PACKAGE) {
                PackageElement packageElement = (PackageElement) enclosingElement;
                packageName = packageElement.getQualifiedName().toString();
            } else {
                parentType = extract(enclosingElement.asType());
                packageName = parentType.packageName;
            }
            return new Type(
                    packageName,
                    element.getSimpleName().toString(),
                    false,
                    annotation,
                    false,
                    false,
                    parameters,
                    parentType,
                    Collections.emptyList(),
                    Collections.emptyList()
            );
        } else if (type.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) type;
            return new Type(
                    "",
                    type.toString(),
                    false,
                    false,
                    true,
                    false,
                    Collections.emptyList(),
                    null,
                    parseBounds(typeVariable.getUpperBound()),
                    parseBounds(typeVariable.getLowerBound())
            );
        } else if (type.getKind() == TypeKind.WILDCARD) {
            WildcardType wildcardType = (WildcardType) type;
            return new Type(
                    "",
                    "?",
                    false,
                    false,
                    false,
                    true,
                    Collections.emptyList(),
                    null,
                    parseBounds(wildcardType.getExtendsBound()),
                    parseBounds(wildcardType.getSuperBound())
            );
        } else {
            throw new UnsupportedOperationException("Type " + type + " is not supported.");
        }
    }

    public static Type fromPackage(String packageName) {
        return new Type(
                packageName,
                "",
                false,
                false,
                false,
                false,
                Collections.emptyList(),
                null,
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    /**
     * 获取包名
     * @return 包名
     */
    @Nonnull
    public String getPackageName() {
        return packageName;
    }

    /**
     * 获取简单名，不包括包名，特别的，对于嵌套类，有如下形式Parent.Nest1.Nest2
     * @return 类名
     */
    @Nonnull
    public String getSimpleName() {
        return simpleName;
    }

    public String getEnclosedSimpleName() {
        if (container == null) {
            return getSimpleName();
        }
        return combine(
                ".",
                container.getEnclosedSimpleName(),
                getSimpleName()
        );
    }

    /**
     * 获取单独类名，若不是嵌套类，则为不包括包名和泛型参数的类名。对于嵌套类，列如 a.b.c.D.E, 返回 D$E
     * @return 单独类名
     */
    public String getEnclosedFlatSimpleName() {
        if (container == null) {
            return getSimpleName();
        }
        return combine(
                "$",
                container.getEnclosedFlatSimpleName(),
                getSimpleName()
        );
    }

    /**
     * 获取最外层类，如果不是嵌套类，则为其本身
     * @return 最外层类
     */
    public Type getTopmostEnclosingType() {
        if (container == null) {
            return this;
        }
        return container.getTopmostEnclosingType();
    }

    private static String combine(String connector, @Nonnull String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part != null && !part.isEmpty()) {
                if (sb.length() == 0) {
                    sb.append(part);
                } else {
                    sb.append(connector).append(part);
                }
            }
        }
        return sb.toString();
    }

    /**
     * 获取全限定类名。特别的，对于嵌套类，例如 a.b.c.D.E 返回 a.b.c.D.E
     * @return 全限定类名
     */
    @Nonnull
    public String getQualifiedName() {
        return combine(
                ".",
                container != null ? container.getQualifiedName() : packageName,
                getSimpleName()
        );
    }

    public boolean isArray() {
        return array;
    }

    public boolean isAnnotation() {
        return annotation;
    }

    public boolean isTypeVar() {
        return typeVar;
    }

    public boolean isWildcard() {
        return wildcard;
    }

    public List<Type> getLowerBounds() {
        return lowerBounds;
    }

    public List<Type> getUpperBounds() {
        return upperBounds;
    }

    public List<Type> getParameters() {
        return parameters;
    }

    public boolean isSamePackage(Type other) {
        return getPackageName().equals(other.getPackageName());
    }

    public boolean isNotObjectType() {
        return !"java.lang".equals(packageName) || !"Object".equals(simpleName);
    }

    public boolean isLangType() {
        return "java.lang".equals(packageName);
    }

    public boolean isBoolean() {
        return packageName.isEmpty() && "boolean".equals(simpleName);
    }

    public boolean isNested() {
        return container != null;
    }

    public boolean isPackage() {
        return simpleName.isEmpty();
    }

    public void openClass(@Nonnull PrintWriter writer, @Nonnull Modifier modifier, @Nonnull Context context, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        writer.print(modifier);
        writer.print(" ");
        writer.print("class ");
        writer.print(getSimpleName());
        printGenericParameters(writer, context, true);
        writer.println(" {");
    }

    public void closeClass(@Nonnull PrintWriter writer, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        writer.println("}");
    }

    private void openConstructor(@Nonnull PrintWriter writer, @Nonnull Modifier modifier, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        Utils.printModifier(writer, modifier);
        writer.print(getSimpleName());
        writer.print("(");
    }

    public void emptyConstructor(@Nonnull PrintWriter writer, @Nonnull Modifier modifier, String indent, int indentNum) {
        openConstructor(writer, modifier, indent, indentNum);
        writer.println(") { }");
    }

    public void fieldsConstructor(@Nonnull PrintWriter writer, @Nonnull Context context, @Nonnull Modifier modifier, @Nonnull List<Property> properties, String indent, int indentNum) {
        if (properties.isEmpty()) {
            return;
        }
        openConstructor(writer, modifier, indent, indentNum);
        writer.println();
        int size = properties.size();
        int i = 0;
        for (Property property : properties) {
            if (!property.isDynamic()) {
                Utils.printIndent(writer, indent, indentNum + 1);
                property.printType(writer, context, true, false);
                writer.print(" ");
                writer.print(context.getMappedFieldName(property));
                if (i != size - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            ++i;
        }
        Utils.printIndent(writer, indent, indentNum);
        writer.println(") {");
        for (Property property : properties) {
            Utils.printIndent(writer, indent, indentNum + 1);
            writer.print("this.");
            writer.print(context.getMappedFieldName(property));
            writer.print(" = ");
            writer.print(context.getMappedFieldName(property));
            writer.println(";");
        }
        Utils.printIndent(writer, indent, indentNum);
        writer.println("}");
    }

    public void copyConstructor(@Nonnull PrintWriter writer, @Nonnull Context context, @Nonnull Modifier modifier, @Nonnull List<Property> properties, String indent, int indentNum) {
        openConstructor(writer, modifier, indent, indentNum);
        writer.print(getSimpleName());
        printGenericParameters(writer, context, false);
        writer.print(" ");
        writer.println("source) {");
        for (Property property : properties) {
            if (!property.isDynamic()) {
                Utils.printIndent(writer, indent, indentNum + 1);
                writer.print("this.");
                writer.print(context.getMappedFieldName(property));
                writer.print(" = source.");
                writer.print(context.getMappedFieldName(property));
                writer.println(";");
            }
        }
        Utils.printIndent(writer, indent, indentNum);
        writer.println("}");
    }

    public void printType(@Nonnull PrintWriter writer, @Nullable Context context, boolean generic, boolean withBound) {
        writer.print(context != null ? context.relativeName(this) : getQualifiedName());
        if (generic) {
            printGenericParameters(writer, context, withBound);
        }
        if (isArray()) {
            writer.print("[]");
        }
    }

    public void printGenericParameters(@Nonnull PrintWriter writer, @Nullable Context context, boolean withBound) {
        if (parameters.isEmpty()) {
            return;
        }
        writer.print("<");
        boolean start = true;
        for (Type parameter : parameters) {
            if (!start) {
                writer.print(", ");
            }
            parameter.printType(writer, context, true, withBound);
            if (withBound) {
                List<Type> upperBounds = parameter.upperBounds;
                List<Type> lowerBounds = parameter.lowerBounds;
                if (!upperBounds.isEmpty()) {
                    writer.print(" extends ");
                    for (int i = 0; i < upperBounds.size(); ++i) {
                        upperBounds.get(i).printType(writer, context, true, true);
                        if (i != upperBounds.size() - 1) {
                            writer.print(" & ");
                        }
                    }
                }
                if (!lowerBounds.isEmpty()) {
                    writer.print(" super ");
                    for (int i = 0; i < lowerBounds.size(); ++i) {
                        lowerBounds.get(i).printType(writer, context, true, true);
                        if (i != lowerBounds.size() - 1) {
                            writer.print(" & ");
                        }
                    }
                }
            }
            start = false;
        }
        writer.print(">");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return array == type.array &&
                typeVar == type.typeVar &&
                wildcard == type.wildcard &&
                Objects.equals(packageName, type.packageName) &&
                Objects.equals(simpleName, type.simpleName) &&
                Objects.equals(parameters, type.parameters) &&
                Objects.equals(container, type.container) &&
                Objects.equals(upperBounds, type.upperBounds) &&
                Objects.equals(lowerBounds, type.lowerBounds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, simpleName, array, typeVar, wildcard, parameters, container, upperBounds, lowerBounds);
    }

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        printType(writer, null, true, true);
        return stringWriter.toString();
    }
}
