package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Type {
    private final String packageName;
    private final String simpleName;
    private final boolean array;
    private final boolean typeVar;
    private final boolean wildcard;
    private final List<Type> parameters;
    private final Type container;
    private final TypeMirror upperBound;
    private final TypeMirror lowerBound;

    /**
     * 构造函数
     * @param packageName 包名
     * @param simpleName 类名，不包括包名，特别的，对于嵌套类，有如下形式Parent.Nest1.Nest2
     * @param array 是否是数组
     * @param parameters 泛型参数列表
     * @param upperBound 类型上界，可为空，此时默认为 {@link Object}
     * @param container 包裹类，可为空
     * @param lowerBound 类型下界，可为空
     */
    public Type(
            @Nonnull String packageName,
            @Nonnull String simpleName,
            boolean array,
            boolean typeVar,
            boolean wildcard,
            @Nonnull List<Type> parameters,
            @Nullable Type container,
            @Nullable TypeMirror upperBound,
            @Nullable TypeMirror lowerBound
    ) {
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.array = array;
        this.typeVar = typeVar;
        this.wildcard = wildcard;
        this.parameters = parameters;
        this.container = container;
        this.upperBound = upperBound;
        this.lowerBound = lowerBound;
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
                typeVar,
                false,
                parameters,
                container,
                upperBound,
                lowerBound
        );
    }

    public Type changeSimpleName(String simpleName) {
        if (wildcard) {
            throw new UnsupportedOperationException();
        }
        return new Type(
                packageName,
                simpleName,
                array,
                typeVar,
                false,
                parameters,
                container,
                upperBound,
                lowerBound
        );
    }

    public Type changePackage(String packageName) {
        return new Type(
                packageName,
                simpleName,
                array,
                typeVar,
                wildcard,
                parameters,
                container,
                upperBound,
                lowerBound
        );
    }

    public Type appendName(String postfix) {
        return new Type(
                packageName,
                simpleName + postfix,
                array,
                typeVar,
                wildcard,
                parameters,
                container,
                upperBound,
                lowerBound
        );
    }

    public String relativeName(Type type, boolean imported) {
        String packageName = type.getPackageName();
        String parentName = type.getPackageWithParent();
        String parentNameOfMe = getPackageWithParent();
        String packageNameOfMe = getPackageName();
        if (packageName.equals(packageNameOfMe)) {
            if (parentName.equals(parentNameOfMe)) {
                return type.getSimpleName();
            }
            if (parentName.startsWith(parentNameOfMe) && parentName.charAt(parentNameOfMe.length()) == '.') {
                return type.getSimpleName();
            }
            if (parentNameOfMe.startsWith(parentName) && parentNameOfMe.charAt(parentName.length()) == '.') {
                return type.getSimpleName();
            }
        }
        return (imported || type.isLangType()) ? type.getEnclosedSimpleName() : type.getQualifiedName();
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
                    Collections.emptyList(),
                    null,
                    null,
                    null
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
                    false,
                    false,
                    parameters,
                    parentType,
                    null,
                    null
            );
        } else if (type.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) type;
            return new Type(
                    "",
                    type.toString(),
                    false,
                    true,
                    false,
                    Collections.emptyList(),
                    null,
                    typeVariable.getUpperBound(), typeVariable.getLowerBound()
            );
        } else if (type.getKind() == TypeKind.WILDCARD) {
            WildcardType wildcardType = (WildcardType) type;
            return new Type(
                    "",
                    "?",
                    false,
                    false,
                    true,
                    Collections.emptyList(),
                    null,
                    wildcardType.getExtendsBound(),
                    wildcardType.getSuperBound()
            );
        } else {
            throw new UnsupportedOperationException("Type " + type + " is not supported.");
        }
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

    public String getPackageWithParent() {
        if (container == null) {
            return packageName;
        }
        return container.getQualifiedName();
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

    /**
     * 获取全限定单独类名。特别的，对于嵌套类，例如 a.b.c.D.E 返回 a.b.c.D$E
     * @return 全限定单独类名
     */
    @Nonnull
    public String getFlatQualifiedName() {
        return combine(
                ".",
                packageName,
                getEnclosedFlatSimpleName()
        );
    }

    public boolean isArray() {
        return array;
    }

    public boolean isTypeVar() {
        return typeVar;
    }

    public boolean isWildcard() {
        return wildcard;
    }

    public TypeMirror getLowerBound() {
        return lowerBound;
    }

    public TypeMirror getUpperBound() {
        return upperBound;
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
            Utils.printIndent(writer, indent, indentNum + 1);
            property.printType(writer, context, true, false);
            writer.print(" ");
            writer.print(context.getMappedFieldName(property));
            if (i != size - 1) {
                writer.println(",");
            } else {
                writer.println();
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
            Utils.printIndent(writer, indent, indentNum + 1);
            writer.print("this.");
            writer.print(context.getMappedFieldName(property));
            writer.print(" = source.");
            writer.print(context.getMappedFieldName(property));
            writer.println(";");
        }
        Utils.printIndent(writer, indent, indentNum);
        writer.println("}");
    }

    public void printType(@Nonnull PrintWriter writer, @Nonnull Context context, boolean generic, boolean withBound) {
        writer.print(context.relativeName(this));
        if (generic) {
            printGenericParameters(writer, context, withBound);
        }
        if (isArray()) {
            writer.print("[]");
        }
    }

    public void printGenericParameters(@Nonnull PrintWriter writer, @Nonnull Context context, boolean withBound) {
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
                TypeMirror upperBound = parameter.upperBound;
                TypeMirror lowerBound = parameter.lowerBound;
                if (upperBound != null && upperBound.getKind() != TypeKind.NONE && upperBound.getKind() != TypeKind.NULL) {
                    if (upperBound.getKind() == TypeKind.INTERSECTION) {
                        IntersectionType intersectionType = (IntersectionType) upperBound;
                        writer.print(" extends ");
                        List<Type> partTypes = intersectionType.getBounds().stream()
                                .map(Type::extract)
                                .filter(Type::isNotObjectType)
                                .collect(Collectors.toList());
                        for (int i = 0; i < partTypes.size(); ++i) {
                            partTypes.get(i).printType(writer, context, true, true);
                            if (i != partTypes.size() - 1) {
                                writer.print(" & ");
                            }
                        }
                    } else {
                        Type bound = Type.extract(upperBound);
                        if (bound.isNotObjectType()) {
                            writer.print(" extends ");
                            bound.printType(writer, context, true, true);
                        }
                    }
                }
                if (lowerBound != null && lowerBound.getKind() != TypeKind.NONE && lowerBound.getKind() != TypeKind.NULL) {
                    Type bound = Type.extract(lowerBound);
                    writer.print(" super ");
                    bound.printType(writer, context, true, true);
                }
            }
            start = false;
        }
        writer.print(">");
    }
}
