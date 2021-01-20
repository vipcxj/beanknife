package io.github.vipcxj.beanknife.core.models;

import com.sun.source.tree.*;
import com.sun.source.util.TreePath;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.github.vipcxj.beanknife.core.utils.ObjectsCompatible;
import io.github.vipcxj.beanknife.core.utils.TreeUtils;
import io.github.vipcxj.beanknife.core.utils.Utils;

import javax.lang.model.element.*;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.stream.Collectors;

public class Type {
    private static final Set<Modifier> DEFAULT_MODIFIERS = new TreeSet<>(Collections.singletonList(Modifier.PUBLIC));
    private final Context context;
    private final Set<Modifier> modifiers;
    private String packageName;
    private String simpleName;
    private int array;
    private final boolean annotation;
    private final boolean typeVar;
    private final boolean wildcard;
    private List<Type> parameters;
    private Type container;
    private final List<Type> upperBounds;
    private final List<Type> lowerBounds;
    private CompilationUnitTree cu;
    private Tree tree;

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
            @NonNull Set<Modifier> modifiers,
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
            @CheckForNull CompilationUnitTree cu,
            @CheckForNull Tree tree
    ) {
        this.context = context;
        this.modifiers = modifiers;
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
        this.cu = cu;
        this.tree = tree;
    }

    private Type(@NonNull Type other) {
        this.context = other.context;
        this.modifiers = other.modifiers;
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
        this.cu = other.cu;
        this.tree = other.tree;
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
        type.cu = null;
        type.tree = null;
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
        type.cu = null;
        type.tree = null;
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
        type.cu = null;
        type.tree = null;
        return type;
    }

    public boolean isStatic() {
        if (container == null) {
            return true;
        }
        if (!container.isStatic()) {
            return false;
        }
        return modifiers.contains(Modifier.STATIC);
    }

    @NonNull
    public Type flatten() {
        if (container == null) {
            return this;
        }
        Type type = new Type(this);
        type.simpleName = getEnclosedFlatSimpleName();
        if (!isStatic()) {
            type.parameters = new ArrayList<>(type.parameters);
            Type parent = container;
            while (parent != null) {
                type.parameters.addAll(0, parent.getParameters());
                parent = parent.container;
            }
        }
        type.container = null;
        type.cu = null;
        type.tree = null;
        return type;
    }

    @NonNull
    public Type withoutParameters() {
        if (parameters.isEmpty()) {
            return this;
        }
        Type type = new Type(this);
        type.parameters = Collections.emptyList();
        type.cu = null;
        type.tree = null;
        return type;
    }

    @NonNull
    public Type withParameters(@NonNull List<Type> parameters) {
        Type type = new Type(this);
        type.parameters = parameters;
        type.cu = null;
        type.tree = null;
        return type;
    }

    @NonNull
    public Type withoutArray() {
        if (!isArray()) {
            return this;
        }
        Type type = new Type(this);
        type.array = 0;
        type.cu = null;
        type.tree = null;
        return type;
    }

    @CheckForNull
    public Type getComponentType() {
        Type type = new Type(this);
        int newArray = type.array - 1;
        if (newArray >= 0) {
            type.array = newArray;
            type.cu = null;
            type.tree = null;
            return type;
        } else {
            return null;
        }
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
                DEFAULT_MODIFIERS,
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
                null,
                null
        );
    }

    public static Type extract(Context context, Class<?> clazz) {
        return extract(context, context.getProcessingEnv().getElementUtils().getTypeElement(clazz.getCanonicalName()), null);
    }

    @NonNull
    public static Type createWildcard(Context context, @NonNull List<Type> extendsBounds, @NonNull List<Type> supperBounds) {
        return new Type(
                context,
                Collections.emptySet(),
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
                null,
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

    public static Type createTypeParameter(Context context, @NonNull String name, @NonNull List<Type> bounds, @CheckForNull CompilationUnitTree cu, @CheckForNull Tree tree) {
        return new Type(
                context,
                Collections.emptySet(),
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
                cu,
                tree
        );
    }

    public static Type extract(@NonNull Context context, @NonNull Element element) {
        return extract(context, element, null);
    }

    private static Tree parseTree(@NonNull Context context, @NonNull Element element) {
        Tree tree = context.trees.getTree(element);
        if (tree != null) {
            return tree;
        }
        if (element.getKind() == ElementKind.PARAMETER) {
            VariableElement variableElement = (VariableElement) element;
            ExecutableElement method = (ExecutableElement) variableElement.getEnclosingElement();
            MethodTree methodTree = context.trees.getTree(method);
            if (methodTree == null) {
                return null;
            }
            for (VariableTree parameter : methodTree.getParameters()) {
                if (parameter.getName().toString().equals(element.getSimpleName().toString())) {
                    return parameter;
                }
            }
        }
        // todo support other element type.
        return null;
    }

    public static Type extract(@NonNull Context context, @NonNull Element element, @Nullable List<Type> parameterTypes) {
        CompilationUnitTree cu = TreeUtils.getCompilationUnit(context, element);
        Tree tree = parseTree(context, element);
        if (element.getKind().isClass() || element.getKind().isInterface()) {
            TypeElement typeElement = (TypeElement) element;
            List<? extends TypeParameterElement> typeParameters = typeElement.getTypeParameters();
            List<Type> parameters;
            parameters = ObjectsCompatible.requireNonNullElseGet(parameterTypes, () -> typeParameters.stream().map(e -> extract(context, e, null)).collect(Collectors.toList()));
            boolean annotation = element.getKind() == ElementKind.ANNOTATION_TYPE;
            Element enclosingElement = element.getEnclosingElement();
            Type parentType = null;
            String packageName;
            if (enclosingElement.getKind() == ElementKind.PACKAGE) {
                PackageElement packageElement = (PackageElement) enclosingElement;
                packageName = packageElement.getQualifiedName().toString();
            } else {
                parentType = extract(context, enclosingElement, null);
                if (parentType == null) {
                    return null;
                }
                packageName = parentType.packageName;
            }
            return new Type(
                    context,
                    typeElement.getModifiers(),
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
                    cu,
                    tree
            );
        } else if (
                element.getKind() == ElementKind.FIELD
                || element.getKind() == ElementKind.PARAMETER
        ) {
            if (tree == null || tree instanceof VariableTree) {
                VariableTree variableTree = (VariableTree) tree;
                tree = variableTree != null ? variableTree.getType() : null;
                return extract(context, element.asType(), cu, tree);
            } else {
                throw new IllegalArgumentException("This is impossible!");
            }
        } else if (element.getKind() == ElementKind.METHOD) {
            ExecutableElement executableElement = (ExecutableElement) element;
            if (tree == null || tree instanceof MethodTree) {
                MethodTree methodTree = (MethodTree) tree;
                tree = methodTree != null ? (methodTree).getReturnType() : null;
                return extract(context, executableElement.getReturnType(), cu, tree);
            } else {
                throw new IllegalArgumentException("This is impossible!");
            }
        } else if (element.getKind() == ElementKind.PACKAGE) {
            PackageElement packageElement = (PackageElement) element;
            return fromPackage(context, packageElement.isUnnamed() ? "" : packageElement.getQualifiedName().toString());
        } else if (element.getKind() == ElementKind.TYPE_PARAMETER) {
            TypeParameterElement typeParameterElement = (TypeParameterElement) element;
            if (tree instanceof TypeParameterTree) {
                TypeParameterTree typeParameterTree = (TypeParameterTree) tree;
                List<Type> bounds = typeParameterTree.getBounds().stream()
                        .map(bound -> {
                            TypeMirror typeMirror = TreeUtils.tryGetTypeMirror(context, element, bound);
                            return extract(context, typeMirror, cu, bound);
                        })
                        .filter(type -> type == null || type.isNotObjectType())
                        .collect(Collectors.toList());
                if (bounds.stream().anyMatch(Objects::isNull)) {
                    return null;
                }
                return createTypeParameter(context, typeParameterElement.getSimpleName().toString(), bounds, cu, tree);
            } else if (tree == null) {
                List<Type> bounds = typeParameterElement.getBounds().stream()
                        .map(bound -> extract(context, bound, null, null))
                        .filter(type -> type == null || type.isNotObjectType())
                        .collect(Collectors.toList());
                if (bounds.stream().anyMatch(Objects::isNull)) {
                    return null;
                }
                return createTypeParameter(context, typeParameterElement.getSimpleName().toString(), bounds, cu, null);
            } else {
                throw new IllegalArgumentException("This is impossible!");
            }
        }
        throw new UnsupportedOperationException("Unsupported element kind: " + element.getKind() + ".");
    }

    @NonNull
    private static List<Type> parseBounds(@NonNull Context context, @Nullable TypeMirror typeMirror, @CheckForNull CompilationUnitTree cu, @NonNull List<Tree> tree) {
        if (typeMirror == null) {
            return Collections.emptyList();
        } else if (typeMirror.getKind() == TypeKind.INTERSECTION) {
            IntersectionType intersectionType = (IntersectionType) typeMirror;
            List<? extends TypeMirror> bounds = intersectionType.getBounds();
            List<Type> types = new ArrayList<>();
            for (int i = 0; i < bounds.size(); ++i) {
                TypeMirror bound = bounds.get(i);
                Tree boundTree = i < tree.size() ? tree.get(i) : null;
                types.add(extract(context, bound, cu, boundTree));
            }
            return types;
        } else {
            return Collections.singletonList(extract(context, typeMirror, cu, tree.isEmpty() ? null : tree.get(0)));
        }
    }

    @CheckForNull
    public static Type extract(@NonNull Context context, @Nullable TypeMirror type, @CheckForNull CompilationUnitTree cu, @CheckForNull Tree tree) {
        if (type == null || type.getKind() == TypeKind.ERROR) {
            return cu != null && tree != null ? context.fixType(cu, tree) : null;
        } else if (type.getKind().isPrimitive()) {
            return new Type(
                    context,
                    Collections.emptySet(),
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
                    cu,
                    tree
            );
        } else if (type.getKind() == TypeKind.ARRAY) {
            ArrayType arrayType = (ArrayType) type;
            ArrayTypeTree arrayTypeTree = (ArrayTypeTree) tree;
            Type componentType = extract(context, arrayType.getComponentType(), cu, arrayTypeTree != null ? arrayTypeTree.getType() : null);
            return componentType != null ? componentType.toArrayType() : null;
        } else if (type.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) type;
            List<? extends Tree> typeParameterTrees = null;
            if (tree != null && tree.getKind() == Tree.Kind.PARAMETERIZED_TYPE) {
                ParameterizedTypeTree parameterizedTypeTree = (ParameterizedTypeTree) tree;
                typeParameterTrees = parameterizedTypeTree.getTypeArguments();
            }
            List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
            List<Type> parameterTypes = new ArrayList<>();
            for (int i = 0; i < typeArguments.size(); ++i) {
                TypeMirror typeArgument = typeArguments.get(i);
                Tree typeParameterTree = typeParameterTrees != null ? typeParameterTrees.get(Math.max(i, typeParameterTrees.size() - 1)) : null;
                Type parameterType = extract(context, typeArgument, cu, cu != null ? typeParameterTree : null);
                if (parameterType == null) {
                    return null;
                }
                parameterTypes.add(parameterType);
            }
            TypeElement typeElement = Utils.toElement(declaredType);
            return extract(context, typeElement, parameterTypes);
        } else if (type.getKind() == TypeKind.TYPEVAR) {
            TypeVariable typeVariable = (TypeVariable) type;
            TypeParameterTree typeParameterTree = tree instanceof TypeParameterTree ? (TypeParameterTree) tree : null;
            List<? extends Tree> boundTrees = typeParameterTree != null ? typeParameterTree.getBounds() : null;
            List<Type> upperBounds = new ArrayList<>();
            TypeMirror upperBound = typeVariable.getUpperBound();
            if (upperBound.getKind() == TypeKind.INTERSECTION) {
                IntersectionType intersectionType = (IntersectionType) upperBound;
                if (boundTrees != null && boundTrees.size() != intersectionType.getBounds().size()) {
                    throw new IllegalArgumentException("This is impossible!");
                }
                for (int i = 0; i < intersectionType.getBounds().size(); ++i) {
                    TypeMirror boundTypeMirror = intersectionType.getBounds().get(i);
                    Tree boundTree = boundTrees != null ? boundTrees.get(i) : null;
                    Type boundType = extract(context, boundTypeMirror, cu, cu != null ? boundTree : null);
                    if (boundType == null) {
                        return null;
                    }
                    upperBounds.add(boundType);
                }
            } else if (!Utils.isThisType(upperBound, Object.class)){
                if (boundTrees != null && boundTrees.size() != 1) {
                    throw new IllegalArgumentException("This is impossible!");
                }
                Tree boundTree = boundTrees != null ? boundTrees.get(0) : null;
                Type boundType = extract(context, upperBound, cu, cu != null ? boundTree : null);
                upperBounds.add(boundType);
            }
            return Type.createTypeParameter(context, typeVariable.asElement().getSimpleName().toString(), upperBounds, cu, tree);
        } else if (type.getKind() == TypeKind.WILDCARD) {
            WildcardType wildcardType = (WildcardType) type;
            TypeMirror extendsBound = wildcardType.getExtendsBound();
            TypeMirror superBound = wildcardType.getSuperBound();
            List<Tree> extendsBoundTrees = Collections.emptyList();
            List<Tree> superBoundTrees = Collections.emptyList();
            if (tree != null) {
                WildcardTree wildcardTree = (WildcardTree) tree;
                Tree bound = wildcardTree.getBound();
                List<Tree> bounds = new ArrayList<>();
                if (bound != null) {
                    if (bound.getKind() == Tree.Kind.INTERSECTION_TYPE) {
                        IntersectionTypeTree intersectionTypeTree = (IntersectionTypeTree) bound;
                        bounds.addAll(intersectionTypeTree.getBounds());
                    } else {
                        bounds.add(bound);
                    }
                }
                if (tree.getKind() == Tree.Kind.EXTENDS_WILDCARD) {
                    extendsBoundTrees = bounds;
                } else if (tree.getKind() == Tree.Kind.SUPER_WILDCARD) {
                    superBoundTrees = bounds;
                }
            }
            List<Type> extendsBoundTypes = extendsBound != null ? parseBounds(context, extendsBound, cu, extendsBoundTrees) : Collections.emptyList();
            List<Type> superBoundTypes = superBound != null ? parseBounds(context, superBound, cu, superBoundTrees) : Collections.emptyList();
            return new Type(
                    context,
                    Collections.emptySet(),
                    "",
                    "?",
                    0,
                    false,
                    false,
                    true,
                    Collections.emptyList(),
                    null,
                    extendsBoundTypes,
                    superBoundTypes,
                    cu,
                    tree
            );
        } else if (type.getKind() == TypeKind.VOID) {
            return null;
        } else {
            throw new UnsupportedOperationException("Type " + type + " is not supported.");
        }
    }

    public TypeMirror getTypeMirror() {
        if (cu != null && tree != null) {
            TreePath path = context.trees.getPath(cu, tree);
            return context.trees.getTypeMirror(path);
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
            TypeMirror typeMirror = typeUtils.getDeclaredType((DeclaredType) containerTypeMirror, typeElement, typeParameters.toArray(new TypeMirror[0]));
            for (int i = 0; i < array; ++i) {
                typeMirror = typeUtils.getArrayType(typeMirror);
            }
            return typeMirror;
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
            return sameType(targetType) || !targetType.isNotObjectType();
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
                Collections.emptySet(),
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
                null,
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

    public Context getContext() {
        return context;
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

    public boolean isObject() {
        return !isPackage() && !isArray() && !isPrimate() && !isWildcard() && !isTypeVar();
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

    public boolean isType(Class<?> type) {
        int array = 0;
        while (type.isArray()) {
            type = type.getComponentType();
            ++array;
        }
        return this.array == array && getQualifiedName().equals(type.getCanonicalName());
    }

    public boolean isView() {
        return context.isViewType(this);
    }

    public boolean sameType(@NonNull Type type) {
        return sameType(type, false);
    }

    public boolean sameType(@NonNull Type type, boolean erasure) {
        if (array != type.array) {
            return false;
        }
        if (!packageName.equals(type.packageName)) {
            return false;
        }
        if (!simpleName.equals(type.simpleName)) {
            return false;
        }
        if (annotation != type.annotation) {
            return false;
        }
        if (typeVar != type.typeVar) {
            return false;
        }
        if (wildcard != type.wildcard) {
            return false;
        }
        if (container == null && type.container != null) {
            return false;
        } else if (container != null && type.container == null) {
            return false;
        } else if (container != null && !container.sameType(type.container, erasure)){
            return false;
        }
        if (!erasure) {
            if (parameters.size() != type.parameters.size()) {
                return false;
            }
            for (int i = 0; i < parameters.size(); ++i) {
                Type parameter = parameters.get(i);
                Type otherParameter = type.parameters.get(i);
                if (!parameter.sameType(otherParameter)) {
                    return false;
                }
            }
            if (upperBounds.size() != type.upperBounds.size()) {
                return false;
            }
            for (int i = 0; i < upperBounds.size(); ++i) {
                Type bound = upperBounds.get(i);
                Type otherParameter = type.upperBounds.get(i);
                if (!bound.sameType(otherParameter)) {
                    return false;
                }
            }
            if (lowerBounds.size() != type.lowerBounds.size()) {
                return false;
            }
            for (int i = 0; i < lowerBounds.size(); ++i) {
                Type bound = lowerBounds.get(i);
                Type otherParameter = type.lowerBounds.get(i);
                if (!bound.sameType(otherParameter)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void openClass(@NonNull PrintWriter writer, @NonNull Modifier modifier, @NonNull Context context, String indent, int indentNum) {
        openClass(writer, modifier, context, null, Collections.emptyList(), indent, indentNum);
    }

    public void openClass(@NonNull PrintWriter writer, @NonNull Modifier modifier, @NonNull Context context, @CheckForNull Type extendsType, @NonNull List<Type> implTypes, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        writer.print(modifier);
        writer.print(" ");
        writer.print("class ");
        writer.print(getSimpleName());
        printGenericParameters(writer, context, true);
        if (extendsType != null) {
            writer.print(" extends ");
            extendsType.printType(writer, context, true, false);
        }
        if (!implTypes.isEmpty()) {
            writer.print(" implements ");
            int i = 0;
            for (Type implType : implTypes) {
                implType.printType(writer, context, true, false);
                if (i++ != implTypes.size() - 1) {
                    writer.print(", ");
                }
            }
        }
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
        properties = properties.stream().filter(property -> !property.isDynamic()).collect(Collectors.toList());
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

    public void printType(@NonNull PrintWriter writer, @CheckForNull Context context, boolean generic, boolean full) {
        if (isStatic() || !generic) {
            writer.print(context != null ? context.relativeName(this) : getQualifiedName());
            if (generic) {
                printGenericParameters(writer, context, full);
            }
        } else {
            if (container == null) {
                throw new IllegalStateException("This is impossible!");
            }
            container.printType(writer, context, true, full);
            writer.print(".");
            writer.print(simpleName);
            printGenericParameters(writer, context, full);
        }
        for (int i = 0; i < array; ++i) {
            writer.print("[]");
        }
    }

    public void startInvokeNew(@NonNull PrintWriter writer, @CheckForNull Context context) {
        writer.print("new ");
        printType(writer, context, false, false);
        if (!getParameters().isEmpty()) {
            writer.print("<>");
        }
        writer.print("(");
    }

    public void endInvokeNew(@NonNull PrintWriter writer) {
        writer.print(")");
    }

    public void printGenericParameters(@NonNull PrintWriter writer, @CheckForNull Context context, boolean full) {
        printGenericParameters(writer, context, full, true);
    }

    public void printGenericParameters(@NonNull PrintWriter writer, @CheckForNull Context context, boolean full, boolean withAngleBrackets) {
        if (parameters.isEmpty()) {
            return;
        }
        if (withAngleBrackets) {
            writer.print("<");
        }
        boolean start = true;
        for (Type parameter : parameters) {
            if (!start) {
                writer.print(", ");
            }
            parameter.printType(writer, context, true, full);
            if (full || !parameter.isTypeVar()) {
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
        if (withAngleBrackets) {
            writer.print(">");
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, packageName, simpleName, array, annotation, typeVar, wildcard, parameters, container, upperBounds, lowerBounds, cu, tree);
    }

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        printType(writer, null, true, true);
        return stringWriter.toString();
    }
}
