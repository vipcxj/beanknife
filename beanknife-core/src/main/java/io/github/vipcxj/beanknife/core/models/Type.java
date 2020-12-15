package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.Utils;

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
    private final Context context;
    private String packageName;
    private String simpleName;
    private boolean array;
    private boolean annotation;
    private boolean typeVar;
    private boolean wildcard;
    private List<Type> parameters;
    private Type container;
    private List<Type> upperBounds;
    private List<Type> lowerBounds;
    private TypeMirror typeMirror;

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
            @NonNull Context context,
            @NonNull String packageName,
            @NonNull String simpleName,
            boolean array,
            boolean annotation,
            boolean typeVar,
            boolean wildcard,
            @NonNull List<Type> parameters,
            @CheckForNull Type container,
            @NonNull List<Type> upperBounds,
            @NonNull List<Type> lowerBounds,
            @CheckForNull TypeMirror typeMirror
    ) {
        this.context = context;
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
        this.typeMirror = typeMirror;
    }

    private Type(@NonNull Type other) {
        this.context = other.context;
        this.packageName = other.packageName;
        this.simpleName = other.simpleName;
        this.array = other.array;
        this.annotation = other.annotation;
        this.typeVar = other.typeVar;
        this.wildcard = other.wildcard;
        this.parameters = other.parameters;
        this.container = other.container;
        this.upperBounds = other.upperBounds;
        this.lowerBounds = other.lowerBounds;
        this.typeMirror = other.typeMirror;
    }

    @NonNull
    public Type toArrayType(@CheckForNull ArrayType arrayType) {
        if (array) return this;
        if (wildcard) {
            throw new UnsupportedOperationException();
        }
        TypeMirror typeMirror = arrayType;
        if (typeMirror == null && this.typeMirror != null) {
            typeMirror = context.getProcessingEnv().getTypeUtils().getArrayType(this.typeMirror);
        }
        Type type = new Type(this);
        type.array = true;
        type.typeMirror = typeMirror;
        return type;
    }

    @NonNull
    public Type changeSimpleName(@NonNull String simpleName, boolean removeParents) {
        if (wildcard) {
            throw new UnsupportedOperationException();
        }
        if (simpleName.equals(this.simpleName) && (!removeParents || this.container == null)) {
            return this;
        }
        Type type = new Type(this);
        type.simpleName = simpleName;
        type.container = removeParents ? null : container;
        type.typeMirror = null;
        return type;
    }

    @NonNull
    public Type changePackage(@NonNull String packageName) {
        if (wildcard || typeVar) {
            throw new UnsupportedOperationException();
        }
        if (packageName.equals(this.packageName)) {
            return this;
        }
        Type type = new Type(this);
        type.packageName = packageName;
        type.typeMirror = null;
        return type;
    }

    @NonNull
    public Type appendName(@NonNull String postfix) {
        if (wildcard) {
            throw new UnsupportedOperationException();
        }
        if (postfix.isEmpty()) {
            return this;
        }
        Type type = new Type(this);
        type.simpleName += postfix;
        type.typeMirror = null;
        return type;
    }

    @NonNull
    public Type flatten() {
        if (container == null) {
            return this;
        }
        Type type = new Type(this);
        type.simpleName = getEnclosedFlatSimpleName();
        type.container = null;
        type.typeMirror = null;
        return type;
    }

    @NonNull
    public Type withoutParameters() {
        if (parameters.isEmpty()) {
            return this;
        }
        Type type = new Type(this);
        type.parameters = Collections.emptyList();
        type.typeMirror = null;
        return type;
    }

    @NonNull
    public Type withParameters(@NonNull List<Type> parameters) {
        Type type = new Type(this);
        type.parameters = parameters;
        return type;
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

    public static Type create(Context context, String packageName, String typeName, boolean array, boolean annotation) {
        return new Type(
                context,
                packageName,
                typeName,
                array,
                annotation,
                false,
                false,
                Collections.emptyList(),
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                null
        );
    }

    public static Type extract(Context context, Class<?> clazz) {
        return extract(context, context.getProcessingEnv().getElementUtils().getTypeElement(clazz.getCanonicalName()).asType());
    }

    private static List<Type> parseBounds(Context context, TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.INTERSECTION) {
            IntersectionType intersectionType = (IntersectionType) typeMirror;
            return intersectionType.getBounds()
                    .stream()
                    .map(t -> Type.extract(context, t))
                    .filter(Type::isNotObjectType)
                    .collect(Collectors.toList());
        } else {
            Type type = Type.extract(context, typeMirror);
            return type.isNotObjectType() ? Collections.singletonList(type) : Collections.emptyList();
        }
    }

    @NonNull
    public static Type createWildcard(Context context, @NonNull List<Type> extendsBounds, @NonNull List<Type> supperBounds) {
        TypeMirror typeMirror, extendsBoundsType = null, superBoundsType = null;
        boolean validExtends = false, validSuper = false;
        if (extendsBounds.isEmpty()) {
            validExtends = true;
        } else if (extendsBounds.size() == 1) {
            extendsBoundsType = extendsBounds.get(0).typeMirror;
            validExtends = extendsBoundsType != null;
        }
        if (supperBounds.isEmpty()) {
            validSuper = true;
        } else if (supperBounds.size() == 1) {
            superBoundsType = supperBounds.get(0).typeMirror;
            validSuper = superBoundsType != null;
        }
        typeMirror = validExtends && validSuper ? context.getProcessingEnv().getTypeUtils().getWildcardType(extendsBoundsType, superBoundsType) : null;
        return new Type(
                context,
                "",
                "?",
                false,
                false,
                false,
                true,
                Collections.emptyList(),
                null,
                extendsBounds,
                supperBounds,
                typeMirror
        );
    }

    @NonNull
    public static Type createUnboundedWildcard(Context context) {
        return createWildcard(context, Collections.emptyList(), Collections.emptyList());
    }

    @NonNull
    public static Type createExtendsWildcard(Context context, @NonNull List<Type> upperBounds) {
        return createWildcard(context, upperBounds, Collections.emptyList());
    }

    @NonNull
    public static Type createSuperWildcard(Context context, @NonNull List<Type> lowerBounds) {
        return createWildcard(context, Collections.emptyList(), lowerBounds);
    }

    public static Type createTypeParameter(Context context, @NonNull String name, @NonNull List<Type> bounds) {
        return new Type(
                context,
                "",
                name,
                false,
                false,
                true,
                false,
                Collections.emptyList(),
                null,
                bounds,
                Collections.emptyList(),
                null
        );
    }

    public static Type extract(Context context, Element element) {
        return null;
    }

    @NonNull
    public static Type extract(Context context, TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return new Type(
                    context,
                    "",
                    type.toString(),
                    false,
                    false,
                    false,
                    false,
                    Collections.emptyList(),
                    null,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    type
            );
        } else if (type.getKind() == TypeKind.ARRAY) {
            ArrayType arrayType = (ArrayType) type;
            return extract(context, arrayType.getComponentType()).toArrayType(arrayType);
        } else if (type.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) type;
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            List<Type> parameters = new ArrayList<>();
            for (TypeMirror typeArgument : typeArguments) {
                parameters.add(extract(context, typeArgument));
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
                parentType = extract(context, enclosingElement.asType());
                packageName = parentType.packageName;
            }
            return new Type(
                    context,
                    packageName,
                    element.getSimpleName().toString(),
                    false,
                    annotation,
                    false,
                    false,
                    parameters,
                    parentType,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    type
            );
        } else if (type.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) type;
            TypeMirror lowerBound = typeVariable.getLowerBound();
            return new Type(
                    context,
                    "",
                    type.toString(),
                    false,
                    false,
                    true,
                    false,
                    Collections.emptyList(),
                    null,
                    parseBounds(context, typeVariable.getUpperBound()),
                    lowerBound.getKind() != TypeKind.NULL ? parseBounds(context, lowerBound) : Collections.emptyList(),
                    type
            );
        } else if (type.getKind() == TypeKind.WILDCARD) {
            WildcardType wildcardType = (WildcardType) type;
            TypeMirror extendsBound = wildcardType.getExtendsBound();
            TypeMirror superBound = wildcardType.getSuperBound();
            return new Type(
                    context,
                    "",
                    "?",
                    false,
                    false,
                    false,
                    true,
                    Collections.emptyList(),
                    null,
                    extendsBound != null ? parseBounds(context, extendsBound) : Collections.emptyList(),
                    superBound != null ? parseBounds(context, superBound) : Collections.emptyList(),
                    type
            );
        } else {
            throw new UnsupportedOperationException("Type " + type + " is not supported.");
        }
    }

    public static Type fromPackage(Context context, String packageName) {
        return new Type(
                context,
                packageName,
                "",
                false,
                false,
                false,
                false,
                Collections.emptyList(),
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                null
        );
    }

    /**
     * 获取包名
     * @return 包名
     */
    @NonNull
    public String getPackageName() {
        return packageName;
    }

    /**
     * 获取简单名，不包括包名，特别的，对于嵌套类，有如下形式Parent.Nest1.Nest2
     * @return 类名
     */
    @NonNull
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

    private static String combine(String connector, @NonNull String... parts) {
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
    @NonNull
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

    public void openClass(@NonNull PrintWriter writer, @NonNull Modifier modifier, @NonNull Context context, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        writer.print(modifier);
        writer.print(" ");
        writer.print("class ");
        writer.print(getSimpleName());
        printGenericParameters(writer, context, true);
        writer.println(" {");
    }

    public void closeClass(@NonNull PrintWriter writer, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        writer.println("}");
    }

    private void openConstructor(@NonNull PrintWriter writer, @NonNull Modifier modifier, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        Utils.printModifier(writer, modifier);
        writer.print(getSimpleName());
        writer.print("(");
    }

    public void emptyConstructor(@NonNull PrintWriter writer, @NonNull Modifier modifier, String indent, int indentNum) {
        openConstructor(writer, modifier, indent, indentNum);
        writer.println(") { }");
    }

    public void fieldsConstructor(@NonNull PrintWriter writer, @NonNull Context context, @NonNull Modifier modifier, @NonNull List<Property> properties, String indent, int indentNum) {
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

    public void copyConstructor(@NonNull PrintWriter writer, @NonNull Context context, @NonNull Modifier modifier, @NonNull List<Property> properties, String indent, int indentNum) {
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

    public void printType(@NonNull PrintWriter writer, @CheckForNull Context context, boolean generic, boolean withBound) {
        writer.print(context != null ? context.relativeName(this) : getQualifiedName());
        if (generic) {
            printGenericParameters(writer, context, withBound);
        }
        if (isArray()) {
            writer.print("[]");
        }
    }

    public void printGenericParameters(@NonNull PrintWriter writer, @CheckForNull Context context, boolean withBound) {
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
