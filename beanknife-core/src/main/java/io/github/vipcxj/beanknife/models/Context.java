package io.github.vipcxj.beanknife.models;

import javax.annotation.Nonnull;
import javax.lang.model.SourceVersion;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import java.io.PrintWriter;
import java.util.*;

public class Context {
    private final Stack<Type> containers;
    private final List<String> imports;
    private final Set<String> symbols;
    private final Stack<Map<String, String>> fieldsStack;
    private final String packageName;

    public Context(@Nonnull String packageName) {
        this.containers = new Stack<>();
        this.imports = new ArrayList<>();
        this.symbols = new HashSet<>();
        this.fieldsStack = new Stack<>();
        this.packageName = packageName;
    }

    public void enter(Type type) {
        if (!packageName.equals(type.getPackageName())) {
            throw new IllegalArgumentException("Package must be " + type.getPackageName() + ".");
        }
        containers.push(type);
        fieldsStack.push(new HashMap<>());
    }

    public void exit() {
        fieldsStack.pop();
        containers.pop();
    }

    public Type getContainer() {
        return containers.peek();
    }

    public Map<String, String> getFields() {
        return fieldsStack.peek();
    }

    public String getMappedFieldName(@Nonnull Property property) {
        return getMappedFieldName(property, property.getName());
    }

    private String getMappedFieldName(@Nonnull Property property, @Nonnull String name) {
        String mappedName = getFields().get(property.getName());
        if (mappedName != null) {
            return mappedName;
        }
        if (SourceVersion.isKeyword(name) || getFields().containsKey(name)) {
            return getMappedFieldName(property, name + "_");
        } else {
            getFields().put(property.getName(), name);
            return name;
        }
    }

    public boolean importVariable(Type name) {
        boolean imported = false;
        if (!name.isTypeVar() && !name.isWildcard()) {
            Type topmostEnclosingType = name.getTopmostEnclosingType();
            String symbol = topmostEnclosingType.getSimpleName();
            String importName = topmostEnclosingType.getQualifiedName();
            if (symbols.contains(symbol)) {
                imported = imports.contains(importName);
            } else if (!name.getPackageName().isEmpty()) {
                imported = true;
                imports.add(importName);
                symbols.add(symbol);
            }
        }
        for (Type parameter : name.getParameters()) {
            importVariable(parameter);
        }
        TypeMirror upperBound = name.getUpperBound();
        if (upperBound != null && upperBound.getKind() != TypeKind.NONE && upperBound.getKind() != TypeKind.NULL) {
            if (upperBound.getKind() == TypeKind.INTERSECTION) {
                IntersectionType intersectionType = (IntersectionType) upperBound;
                for (TypeMirror bound : intersectionType.getBounds()) {
                    importVariable(Type.extract(bound));
                }
            } else {
                importVariable(Type.extract(upperBound));
            }
        }
        TypeMirror lowerBound = name.getLowerBound();
        if (lowerBound != null && lowerBound.getKind() != TypeKind.NONE && lowerBound.getKind() != TypeKind.NULL) {
            importVariable(Type.extract(lowerBound));
        }
        return imported;
    }

    public String relativeName(Type name) {
        return getContainer().relativeName(name, importVariable(name));
    }

    public boolean print(@Nonnull PrintWriter writer) {
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
                        && !anImport.startsWith("java.lang")
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
}
