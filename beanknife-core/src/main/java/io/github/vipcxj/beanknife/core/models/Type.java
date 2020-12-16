package io.github.vipcxj.beanknife.core.models;

import com.sun.source.tree.ParameterizedTypeTree;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.Utils;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Type {
    private final Context context;
    private String packageName;
    private String simpleName;
    private int array;
    private boolean annotation;
    private boolean typeVar;
    private boolean wildcard;
    private List<Type> parameters;
    private Type container;
    private List<Type> upperBounds;
    private List<Type> lowerBounds;
    private Element element;

    /**
     * 构造函数
     * @param packageName 包名
     * @param simpleName 类名，不包括包名，特别的，对于嵌套类，有如下形式Parent.Nest1.Nest2
     * @param array 数组维数，若不是数组，则为0
     * @param parameters 泛型参数列表
     * @param upperBounds 类型上界，可为空，此时默认为 {@link Object}
     * @param container 包裹类，可为空
     * @param lowerBounds 类型下界，可为空
     */
    public Type(
            @NonNull Context context,
            @NonNull String packageName,
            @NonNull String simpleName,
            int array,
            boolean annotation,
            boolean typeVar,
            boolean wildcard,
            @NonNull List<Type> parameters,
            @CheckForNull Type container,
            @NonNull List<Type> upperBounds,
            @NonNull List<Type> lowerBounds,
            @CheckForNull Element element
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
        this.element = element;
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
        this.element = other.element;
    }

    @NonNull
    public Type toArrayType() {
        if (wildcard) {
            throw new UnsupportedOperationException();
        }
        Type type = new Type(this);
        ++type.array;
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
        type.element = null;
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
        type.element = null;
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
        type.element = null;
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
        type.element = null;
        return type;
    }

    @NonNull
    public Type withoutParameters() {
        if (parameters.isEmpty()) {
            return this;
        }
        Type type = new Type(this);
        type.parameters = Collections.emptyList();
        type.element = null;
        return type;
    }

    @NonNull
    public Type withParameters(@NonNull List<Type> parameters) {
        Type type = new Type(this);
        type.parameters = parameters;
        type.element = null;
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

    public static Type create(Context context, String packageName, String typeName, int array, boolean annotation) {
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
        return extract(context, context.getProcessingEnv().getElementUtils().getTypeElement(clazz.getCanonicalName()));
    }

    @NonNull
    public static Type createWildcard(Context context, @NonNull List<Type> extendsBounds, @NonNull List<Type> supperBounds) {
        return new Type(
                context,
                "",
                "?",
                0,
                false,
                false,
                true,
                Collections.emptyList(),
                null,
                extendsBounds,
                supperBounds,
                null
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
                0,
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

    public static Type extract(@NonNull Context context, @NonNull Element element) {
        if (element.getKind().isClass() || element.getKind().isInterface()) {
            TypeElement typeElement = (TypeElement) element;
            List<? extends TypeParameterElement> typeParameters = typeElement.getTypeParameters();
            List<Type> parameters = typeParameters.stream().map(e -> extract(context, e)).collect(Collectors.toList());
            boolean annotation = element.getKind() == ElementKind.ANNOTATION_TYPE;
            Element enclosingElement = element.getEnclosingElement();
            Type parentType = null;
            String packageName;
            if (enclosingElement.getKind() == ElementKind.PACKAGE) {
                PackageElement packageElement = (PackageElement) enclosingElement;
                packageName = packageElement.getQualifiedName().toString();
            } else {
                parentType = extract(context, enclosingElement);
                if (parentType == null) {
                    return null;
                }
                packageName = parentType.packageName;
            }
            return new Type(
                    context,
                    packageName,
                    element.getSimpleName().toString(),
                    0,
                    annotation,
                    false,
                    false,
                    parameters,
                    parentType,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    element
            );
        } else if (
                element.getKind() == ElementKind.FIELD
                || element.getKind() == ElementKind.ENUM_CONSTANT
                || element.getKind() == ElementKind.PARAMETER
                || element.getKind() == ElementKind.LOCAL_VARIABLE
                || element.getKind() == ElementKind.RESOURCE_VARIABLE
                || element.getKind() == ElementKind.EXCEPTION_PARAMETER
        ) {
            return extract(context, element, element.asType());
        } else if (element.getKind() == ElementKind.METHOD) {
            ExecutableElement executableElement = (ExecutableElement) element;
            return extract(context, executableElement, executableElement.getReturnType());
        } else if (element.getKind() == ElementKind.PACKAGE) {
            PackageElement packageElement = (PackageElement) element;
            return fromPackage(context, packageElement.isUnnamed() ? "" : packageElement.getQualifiedName().toString());
        } else if (element.getKind() == ElementKind.TYPE_PARAMETER) {
            TypeParameterElement typeParameterElement = (TypeParameterElement) element;
            List<Type> bounds = typeParameterElement.getBounds()
                    .stream()
                    .map(bound -> extract(context, typeParameterElement, bound))
                    .filter(type -> type == null || type.isNotObjectType())
                    .collect(Collectors.toList());
            if (bounds.stream().anyMatch(Objects::isNull)) {
                return null;
            }
            return createTypeParameter(context, typeParameterElement.getSimpleName().toString(), bounds);
        }
        throw new UnsupportedOperationException("Unsupported element kind: " + element.getKind() + ".");
    }

    @CheckForNull
    public static Type extract(@NonNull Context context, @CheckForNull Element element, @NonNull TypeMirror type) {
        if (type.getKind().isPrimitive()) {
            return new Type(
                    context,
                    "",
                    type.toString(),
                    0,
                    false,
                    false,
                    false,
                    Collections.emptyList(),
                    null,
                    Collections.emptyList(),
                    Collections.emptyList(),
                    element
            );
        } else if (type.getKind() == TypeKind.ARRAY) {
            ArrayType arrayType = (ArrayType) type;
            Type componentType = extract(context, element, arrayType.getComponentType());
            return componentType != null ? componentType.toArrayType() : null;
        } else if (type.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) type;
            Element theElement = declaredType.asElement();
            if (theElement.getKind().isClass() || theElement.getKind().isInterface()) {
                return extract(context, theElement);
            } else {
                throw new IllegalStateException("This is impossible. The element kind is " + theElement.getKind());
            }
        } else if (type.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) type;
            Element theElement = typeVariable.asElement();
            if (theElement.getKind() == ElementKind.TYPE_PARAMETER) {
                return extract(context, theElement);
            } else {
                throw new IllegalStateException("This is impossible. The element kind is " + theElement.getKind());
            }
//        } else if (type.getKind() == TypeKind.WILDCARD) {
//            WildcardType wildcardType = (WildcardType) type;
//            TypeMirror extendsBound = wildcardType.getExtendsBound();
//            TypeMirror superBound = wildcardType.getSuperBound();
//            return new Type(
//                    context,
//                    "",
//                    "?",
//                    false,
//                    false,
//                    false,
//                    true,
//                    Collections.emptyList(),
//                    null,
//                    extendsBound != null ? parseBounds(context, extendsBound) : Collections.emptyList(),
//                    superBound != null ? parseBounds(context, superBound) : Collections.emptyList(),
//                    type
//            );
        } else if (type.getKind() == TypeKind.VOID) {
            return null;
        } else if (type.getKind() == TypeKind.ERROR) {
            return element != null ? context.extractType(element) : null;
        } else {
            throw new UnsupportedOperationException("Type " + type + " is not supported.");
        }
    }

    public TypeMirror getTypeMirror() {
        if (element != null) {
            if (element.getKind() == ElementKind.METHOD) {
                ExecutableElement executableElement = (ExecutableElement) this.element;
                return executableElement.getReturnType();
            } else {
                return element.asType();
            }
        } else if (isPrimate()) {
            Types typeUtils = context.getProcessingEnv().getTypeUtils();
            TypeMirror typeMirror;
            if (isBoolean()) {
                typeMirror = typeUtils.getPrimitiveType(TypeKind.BOOLEAN);
            } else if (isChar()) {
                typeMirror = typeUtils.getPrimitiveType(TypeKind.CHAR);
            } else if (isByte()) {
                typeMirror = typeUtils.getPrimitiveType(TypeKind.BYTE);
            } else if (isShort()) {
                typeMirror = typeUtils.getPrimitiveType(TypeKind.SHORT);
            } else if (isInt()) {
                typeMirror = typeUtils.getPrimitiveType(TypeKind.INT);
            } else if (isLong()) {
                typeMirror = typeUtils.getPrimitiveType(TypeKind.LONG);
            } else if (isFloat()) {
                typeMirror = typeUtils.getPrimitiveType(TypeKind.FLOAT);
            } else if (isDouble()) {
                typeMirror = typeUtils.getPrimitiveType(TypeKind.DOUBLE);
            } else {
                throw new IllegalArgumentException("This is impossible.");
            }
            for (int i = 0; i < array; ++i) {
                typeMirror = typeUtils.getArrayType(typeMirror);
            }
            return typeMirror;
        } else if (!isTypeVar() && !isWildcard()) {
            Types typeUtils = context.getProcessingEnv().getTypeUtils();
            Elements elementUtils = context.getProcessingEnv().getElementUtils();
            TypeElement typeElement = elementUtils.getTypeElement(getQualifiedName());
            if (typeElement == null) {
                return null;
            }
            List<TypeMirror> typeParameters = parameters.stream().map(Type::getTypeMirror).collect(Collectors.toList());
            if (typeParameters.stream().anyMatch(Objects::isNull)) {
                return null;
            }
            TypeMirror containerTypeMirror = null;
            if (container != null) {
                containerTypeMirror = container.getTypeMirror();
                if (containerTypeMirror == null || containerTypeMirror.getKind() != TypeKind.DECLARED) {
                    return null;
                }
            }
            return typeUtils.getDeclaredType((DeclaredType) containerTypeMirror, typeElement, typeParameters.toArray(new TypeMirror[0]));
        } else {
            return null;
        }
    }

    public boolean canAssignTo(TypeMirror target) {
        TypeMirror typeMirror = getTypeMirror();
        if (typeMirror != null) {
            Types typeUtils = context.getProcessingEnv().getTypeUtils();
            return typeUtils.isAssignable(typeMirror, target);
        } else {
            return Utils.isThisType(target, Object.class);
        }
    }

    public boolean canAssignTo(Type targetType) {
        TypeMirror typeMirror = getTypeMirror();
        TypeMirror targetTypeMirror = targetType.getTypeMirror();
        if (typeMirror != null && targetTypeMirror != null) {
            Types typeUtils = context.getProcessingEnv().getTypeUtils();
            return typeUtils.isAssignable(typeMirror, targetTypeMirror);
        } else {
            return !targetType.isNotObjectType();
        }
    }

    public boolean canBeAssignedBy(TypeMirror source) {
        TypeMirror typeMirror = getTypeMirror();
        if (typeMirror != null) {
            Types typeUtils = context.getProcessingEnv().getTypeUtils();
            return typeUtils.isAssignable(source, typeMirror);
        } else {
            return !isNotObjectType();
        }
    }

    public static Type fromPackage(Context context, String packageName) {
        return new Type(
                context,
                packageName,
                "",
                0,
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

    public int getArray() {
        return array;
    }

    public boolean isArray() {
        return array > 0;
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

    public boolean isChar() {
        return packageName.isEmpty() && "char".equals(simpleName);
    }

    public boolean isByte() {
        return packageName.isEmpty() && "byte".equals(simpleName);
    }

    public boolean isShort() {
        return packageName.isEmpty() && "short".equals(simpleName);
    }

    public boolean isInt() {
        return packageName.isEmpty() && "int".equals(simpleName);
    }

    public boolean isLong() {
        return packageName.isEmpty() && "long".equals(simpleName);
    }

    public boolean isFloat() {
        return packageName.isEmpty() && "float".equals(simpleName);
    }

    public boolean isDouble() {
        return packageName.isEmpty() && "double".equals(simpleName);
    }

    public boolean isNested() {
        return container != null;
    }

    public boolean isPackage() {
        return simpleName.isEmpty();
    }

    public boolean isPrimate() {
        return packageName.isEmpty()
                && !typeVar
                && !wildcard
                && parameters.isEmpty()
                && container == null
                && upperBounds.isEmpty()
                && lowerBounds.isEmpty()
                && (simpleName.equals("boolean")
                || simpleName.equals("char")
                || simpleName.equals("byte")
                || simpleName.equals("short")
                || simpleName.equals("int")
                || simpleName.equals("long")
                || simpleName.equals("float")
                || simpleName.equals("double"));
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
        for (int i = 0; i < array; ++i) {
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
