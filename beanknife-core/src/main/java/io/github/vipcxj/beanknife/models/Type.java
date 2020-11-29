package io.github.vipcxj.beanknife.models;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.lang.model.element.*;
import javax.lang.model.type.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    private PackageManager packageManager;

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

    public Type withPackageManager(PackageManager manager) {
        this.packageManager = manager;
        return this;
    }

/*    @Nonnull
    public static Type extract(ProcessingEnvironment environment, TypeElement element) {
        String packageName = environment.getElementUtils().getPackageOf(element).getQualifiedName().toString();
        String qualifiedName = element.getQualifiedName().toString();
        DeclaredType type = (DeclaredType) element.asType();
        List<Type> parameters = new ArrayList<>();
        for (TypeMirror typeArgument : type.getTypeArguments()) {
            if (typeArgument.getKind() == TypeKind.DECLARED) {
                parameters.add(extract(environment, (TypeElement) ((DeclaredType) typeArgument).asElement()));
            } else if (typeArgument.getKind() == TypeKind.ARRAY) {
            }
        }

        return new Type(packageName, packageName.isEmpty() ? qualifiedName : qualifiedName.substring(packageName.length() + 1));
    }*/

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
    public String getSimpleName(boolean withParameter) {
        if (!withParameter) {
            return simpleName;
        }
        return simpleName + getParametersPostfix();
    }

    public String getParametersPostfix() {
        if (parameters.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<");
        boolean start = true;
        for (Type parameter : parameters) {
            if (!start) {
                sb.append(", ");
            }
            boolean imported = false;
            if (packageManager != null) {
                parameter = parameter.withPackageManager(packageManager);
                imported = packageManager.importVariable(parameter);
            }
            if (imported) {
                sb.append(parameter.getEnclosedSimpleName(true, true));
            } else {
                sb.append(parameter.getQualifiedName(true, true));
            }
            start = false;
        }
        sb.append(">");
        return sb.toString();
    }

    public String getEnclosedSimpleName(boolean withParameters, boolean withParentParameters) {
        if (container == null) {
            return getSimpleName(withParameters);
        }
        return combine(
                ".",
                container.getEnclosedSimpleName(withParentParameters, withParentParameters),
                getSimpleName(withParameters)
        );
    }

    /**
     * 获取单独类名，若不是嵌套类，则为不包括包名和泛型参数的类名。对于嵌套类，列如 a.b.c.D.E, 返回 D$E
     * @return 单独类名
     */
    public String getEnclosedFlatSimpleName(boolean withParameters) {
        if (container == null) {
            return getSimpleName(false);
        }
        return combine(
                "$",
                container.getEnclosedFlatSimpleName(false),
                getSimpleName(withParameters)
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
    public String getQualifiedName(boolean withParameters, boolean withParentParameters) {
        return combine(
                ".",
                container != null ? container.getQualifiedName(withParentParameters, withParentParameters) : packageName,
                getSimpleName(withParameters)
        );
    }

    /**
     * 获取全限定单独类名。特别的，对于嵌套类，例如 a.b.c.D.E 返回 a.b.c.D$E
     * @return 全限定单独类名
     */
    @Nonnull
    public String getFlatQualifiedName(boolean withParameters) {
        return combine(
                ".",
                packageName,
                getEnclosedFlatSimpleName(withParameters)
        );
    }

}
