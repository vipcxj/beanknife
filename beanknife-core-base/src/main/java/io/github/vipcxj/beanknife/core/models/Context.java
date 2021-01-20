package io.github.vipcxj.beanknife.core.models;

import com.sun.source.tree.*;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.TreeUtils;
import io.github.vipcxj.beanknife.core.utils.Utils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Context {

    final static String INDENT = "    ";

    private final List<String> imports;
    private final Set<String> symbols;
    private final Map<Property, String> fields;
    protected final Trees trees;
    protected final ProcessingEnvironment processingEnv;
    protected final ProcessorData processorData;
    protected String packageName;
    private final List<Property> properties;
    protected final Stack<Type> containers;
    protected final List<String> errors;
    private String configureBeanFieldVar;
    private String configureBeanGetterVar;
    private boolean locked;
    private final Map<String, Object> subContexts;

    public Context(@NonNull Trees trees, @NonNull ProcessingEnvironment processingEnv, ProcessorData processorData) {
        this.imports = new ArrayList<>();
        this.symbols = new HashSet<>();
        this.fields = new IdentityHashMap<>();
        this.trees = trees;
        this.processingEnv = processingEnv;
        this.processorData = processorData;
        this.properties = new LinkedList<>();
        this.containers = new Stack<>();
        this.errors = new ArrayList<>();
        this.locked = false;
        this.subContexts = new HashMap<>();
    }

    public void enter(Type type) {
        if (!packageName.equals(type.getPackageName())) {
            throw new IllegalArgumentException("Package must be " + type.getPackageName() + ".");
        }
        containers.push(type);
    }

    public void exit() {
        containers.pop();
    }

    public void setContext(String key, Object ctx) {
        subContexts.put(key, ctx);
    }

    public <T> T getContext(String key) {
        //noinspection unchecked
        return (T) subContexts.get(key);
    }

    public <T> T getOrCreateLongTermContext(String key, Supplier<T> creator) {
        return processorData.getOrCreateSubContext(key, creator);
    }

    public Type getContainer() {
        return containers.peek();
    }

    public String getMappedFieldName(@NonNull Property property) {
        return getMappedFieldName(property, property.getName());
    }

    private String getMappedFieldName(@NonNull Property property, @NonNull String name) {
        String mappedName = fields.get(property);
        if (mappedName != null) {
            return mappedName;
        }
        if (SourceVersion.isKeyword(name) || fields.containsValue(name)) {
            return getMappedFieldName(property, name + "_");
        } else {
            fields.put(property, name);
            return name;
        }
    }

    public boolean isImported(String importName) {
        return imports.contains(importName);
    }

    public void importVariable(String importName, String symbol) {
        if (!symbols.contains(symbol)) {
            imports.add(importName);
            symbols.add(symbol);
        }
    }

    public boolean isImported(Type name) {
        if (name.getPackageName().equals(packageName)) {
            return true;
        }
        boolean imported = false;
        if (!name.isTypeVar() && !name.isWildcard()) {
            Type topmostEnclosingType = name.getTopmostEnclosingType();
            String symbol = topmostEnclosingType.getSimpleName();
            String importName = topmostEnclosingType.getQualifiedName();
            if (symbols.contains(symbol)) {
                imported = imports.contains(importName);
            }
        }
        return imported;
    }

    public void importVariable(Type name) {
        boolean imported = false;
        if (!name.isTypeVar() && !name.isWildcard()) {
            Type topmostEnclosingType = name.getTopmostEnclosingType();
            String symbol = topmostEnclosingType.getSimpleName();
            String importName = topmostEnclosingType.getQualifiedName();
            if (!symbols.contains(symbol) && !name.getPackageName().isEmpty()) {
                imports.add(importName);
                symbols.add(symbol);
            }
        }
        for (Type parameter : name.getParameters()) {
            importVariable(parameter);
        }
        for (Type upperBound : name.getUpperBounds()) {
            importVariable(upperBound);
        }
        for (Type lowerBound : name.getLowerBounds()) {
            importVariable(lowerBound);
        }
    }

    public void addProperty(Property property, boolean override) {
        if (locked) {
            throw new IllegalStateException("Locked! Add property is not allowed.");
        }
        Elements elementUtils = processingEnv.getElementUtils();
        boolean done = false;
        ListIterator<Property> iterator = properties.listIterator();
        while (iterator.hasNext()) {
            Property p = iterator.next();
            if (elementUtils.hides(property.getElement(), p.getElement())) {
                iterator.remove();
                if (Utils.isNotObjectProperty(property)) {
                    iterator.add(p.overrideBy(property));
                }
                done = true;
                break;
            } else if (elementUtils.hides(p.getElement(), property.getElement())) {
                done = true;
                break;
            } else if (p.getGetterName().equals(property.getGetterName())) {
                if (override || (!p.isMethod() && property.isMethod())) {
                    iterator.remove();
                    if (Utils.isNotObjectProperty(property)) {
                        iterator.add(p.overrideBy(property));
                    }
                } else if (p.isMethod() == property.isMethod()) {
                    Element ownerP = p.getElement().getEnclosingElement();
                    Element ownerProperty = property.getElement().getEnclosingElement();
                    error("Property conflict: "
                            + (ownerP != null
                            ? p.getElement().getSimpleName() + " in " + ownerP.getSimpleName()
                            : p.getElement().getSimpleName())
                            + " / "
                            + (ownerProperty != null
                            ? property.getElement().getSimpleName() + " in " + ownerProperty.getSimpleName()
                            : property.getElement().getSimpleName()));
                }
                done = true;
                break;
            }
        }
        if (!done && Utils.isNotObjectProperty(property)) {
            properties.add(property);
        }
    }

    public String getConfigureBeanFieldVar() {
        return configureBeanFieldVar;
    }

    public String getConfigureBeanGetterVar() {
        return configureBeanGetterVar;
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        if (locked) {
            throw new IllegalStateException("Already locked!");
        }
        configureBeanFieldVar = calcNewVar("cachedConfigureBean");
        // should not be serialized, so should not named start with get.
        configureBeanGetterVar = calcNewMethod("gottenCachedConfigureBean");
        locked = true;
    }

    private String calcNewVar(String varName, String... otherVars) {
        for (Property property : properties) {
            String fieldName = getMappedFieldName(property);
            if (Objects.equals(fieldName, varName)) {
                return calcNewVar(varName + "_", otherVars);
            }
        }
        for (String otherVar : otherVars) {
            if (Objects.equals(otherVar, varName)) {
                return calcNewVar(varName + "_", otherVars);
            }
        }
        return varName;
    }

    private String calcNewMethod(String methodName, String... otherMethods) {
        for (Property property : properties) {
            if (Objects.equals(property.getGetterName(), methodName)) {
                return calcNewMethod(methodName + "_", otherMethods);
            }
        }
        for (String otherGetter : otherMethods) {
            if (Objects.equals(otherGetter, methodName)) {
                return calcNewMethod(methodName + "_", otherMethods);
            }
        }
        return methodName;
    }

    public boolean hasImport(String importedName) {
        return imports.contains(importedName);
    }

    public String getImportedName(String importedName, String simpleName) {
        if (hasImport(importedName)) {
            return simpleName;
        } else {
            return importedName;
        }
    }

    public String relativeName(Type name) {
        return getContainer().relativeName(name, isImported(name));
    }

    public boolean print(@NonNull PrintWriter writer) {
        boolean printed = false;
        if (!packageName.isEmpty()) {
            printed = true;
            writer.print("package ");
            writer.print(packageName);
            writer.print(";");
            writer.println();
        }
        if (!imports.isEmpty()) {
            boolean imported = false;
            imports.sort(String::compareTo);
            for (String anImport : imports) {
                if ((!anImport.startsWith(packageName)
                        || (anImport.length() > packageName.length() + 1 && anImport.substring(packageName.length() + 1).indexOf('.') != -1))
                        && !(anImport.startsWith("java.lang.") && anImport.indexOf('.', 10) == -1)
                ) {
                    if (!imported) {
                        imported = true;
                        printed = true;
                        writer.println();
                    }
                    writer.print("import ");
                    writer.print(anImport);
                    writer.println(";");
                }
            }
        }
        return printed;
    }

    @NonNull
    public String getPackageName() {
        return packageName;
    }

    public List<Property> getProperties() {
        return properties;
    }

    @CheckForNull
    public Property getProperty(String name) {
        for (Property property : properties) {
            if (property.getName().equals(name)) {
                return property;
            }
        }
        return null;
    }

    public ProcessingEnvironment getProcessingEnv() {
        return processingEnv;
    }

    public Trees getTrees() {
        return trees;
    }

    private Type fixType(Set<String> imports, String packageName, String typeName) {
        String fixedTypeName = processorData != null ? processorData.fixType(imports, packageName, typeName) : null;
        if (fixedTypeName != null) {
            int index = fixedTypeName.lastIndexOf('.');
            if (index != -1) {
                String pName = fixedTypeName.substring(0, index);
                String tName = fixedTypeName.substring(index + 1);
                return Type.create(this,  pName, tName, 0, false);
            } else {
                return Type.create(this, "", fixedTypeName, 0, false);
            }
        } else {
            return null;
        }
    }

    private Type tryGetTypeMirror(CompilationUnitTree compilationUnit, Tree tree) {
        TreePath path = trees.getPath(compilationUnit, tree);
        TypeMirror typeMirror = trees.getTypeMirror(path);
        if (
                typeMirror != null
                && typeMirror.getKind() != TypeKind.ERROR
                && typeMirror.getKind() != TypeKind.VOID
                && typeMirror.getKind() != TypeKind.NONE
                && typeMirror.getKind() != TypeKind.NULL
        ) {
            return Type.extract(this, typeMirror, compilationUnit, tree);
        } else {
            return null;
        }
    }

    private Type fixType(CompilationUnitTree compilationUnit, Set<String> imports, String packageName, Tree tree) {
        Type type = tryGetTypeMirror(compilationUnit, tree);
        if (type != null) {
            return type;
        }
        if (tree.getKind() == Tree.Kind.PARAMETERIZED_TYPE) {
            ParameterizedTypeTree parameterizedTypeTree = (ParameterizedTypeTree) tree;
            Type baseType = fixType(compilationUnit, imports, packageName, parameterizedTypeTree.getType());
            List<Type> parameters = parameterizedTypeTree.getTypeArguments()
                    .stream()
                    .map(t -> fixType(compilationUnit, imports, packageName, t))
                    .collect(Collectors.toList());
            if (baseType == null || parameters.stream().anyMatch(Objects::isNull)) {
                return null;
            } else {
                return baseType.withParameters(parameters);
            }
        } else if (tree.getKind() == Tree.Kind.MEMBER_SELECT) {
            String typeName = TreeUtils.parseMemberSelect((MemberSelectTree) tree);
            return fixType(imports, packageName, typeName);
        } else if (tree.getKind() == Tree.Kind.IDENTIFIER) {
            IdentifierTree identifierTree = (IdentifierTree) tree;
            return fixType(imports, packageName, identifierTree.getName().toString());
        } else if (tree.getKind() == Tree.Kind.UNBOUNDED_WILDCARD) {
            return Type.createUnboundedWildcard(this);
        } else if (tree.getKind() == Tree.Kind.EXTENDS_WILDCARD || tree.getKind() == Tree.Kind.SUPER_WILDCARD) {
            WildcardTree wildcardTree = (WildcardTree) tree;
            Tree bounds = wildcardTree.getBound();
            if (bounds == null) {
                throw new IllegalStateException("This is impossible!");
            }
            List<Type> boundTypes;
            if (bounds.getKind() == Tree.Kind.INTERSECTION_TYPE) {
                IntersectionTypeTree intersectionTypeTree = (IntersectionTypeTree) bounds;
                boundTypes = intersectionTypeTree.getBounds()
                        .stream()
                        .map(bound -> fixType(compilationUnit, imports, packageName, bound))
                        .collect(Collectors.toList());
            } else {
                boundTypes = Collections.singletonList(fixType(compilationUnit, imports, packageName, bounds));
            }
            if (boundTypes.stream().anyMatch(Objects::isNull)) {
                return null;
            }
            if (tree.getKind() == Tree.Kind.EXTENDS_WILDCARD) {
                return Type.createExtendsWildcard(this, boundTypes);
            } else {
                return Type.createSuperWildcard(this, boundTypes);
            }
        } else if (tree.getKind() == Tree.Kind.TYPE_PARAMETER) {
            TypeParameterTree typeParameterTree = (TypeParameterTree) tree;
            List<Type> boundTypes = typeParameterTree.getBounds()
                    .stream()
                    .map(bound -> fixType(compilationUnit, imports, packageName, bound))
                    .collect(Collectors.toList());
            if (boundTypes.stream().anyMatch(Objects::isNull)) {
                return null;
            }
            return Type.createTypeParameter(this, typeParameterTree.getName().toString(), boundTypes, compilationUnit, tree);
        } else {
            throw new UnsupportedOperationException("Unsupported tree kind: " + tree.getKind() + ".");
        }
    }

    @CheckForNull
    public Type fixType(@NonNull CompilationUnitTree compilationUnit, @NonNull Tree tree) {
        String packageName = TreeUtils.parsePackageName(compilationUnit);
        Set<String> imports = compilationUnit.getImports().stream().map(TreeUtils::parseImport).collect(Collectors.toSet());
        return fixType(compilationUnit, imports, packageName, tree);
    }

    public void error(@NonNull String message) {
        errors.add(message);
        Utils.logWarn(getProcessingEnv(), message);
    }

    public boolean isViewType(Type type) {
        return !type.isArray() && getViewData(type) != null;
    }

    public ViewOfData getViewData(Type type) {
        return processorData.getByGenName(type.getQualifiedName());
    }
}
