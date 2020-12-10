package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.PrintWriter;
import java.util.*;

public class Context {

    final static String INDENT = "    ";

    private final List<String> imports;
    private final Set<String> symbols;
    private final Stack<Map<String, String>> fieldsStack;
    private final ProcessingEnvironment processingEnv;
    protected String packageName;
    private final List<Property> properties;
    protected final Stack<Type> containers;

    public Context(@Nonnull ProcessingEnvironment processingEnv) {
        this.imports = new ArrayList<>();
        this.symbols = new HashSet<>();
        this.fieldsStack = new Stack<>();
        this.processingEnv = processingEnv;
        this.properties = new LinkedList<>();
        this.containers = new Stack<>();
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

    public String getMappedFieldName(@Nonnull String property) {
        return getMappedFieldName(property, property);
    }

    public String getMappedFieldName(@Nonnull Property property) {
        return getMappedFieldName(property.getName(), property.getName());
    }

    private String getMappedFieldName(@Nonnull String property, @Nonnull String name) {
        String mappedName = getFields().get(property);
        if (mappedName != null) {
            return mappedName;
        }
        if (SourceVersion.isKeyword(name) || getFields().containsKey(name)) {
            return getMappedFieldName(property, name + "_");
        } else {
            getFields().put(property, name);
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

    public void addProperty(Property property, boolean override) {
        Elements elementUtils = processingEnv.getElementUtils();
        boolean done = false;
        ListIterator<Property> iterator = properties.listIterator();
        while (iterator.hasNext()) {
            Property p = iterator.next();
            if (elementUtils.hides(property.getElement(), p.getElement())) {
                iterator.remove();
                if (Utils.isNotObjectProperty(property)) {
                    iterator.add(new Property(property, p.getComment()));
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
                        iterator.add(new Property(property, p.getComment()));
                    }
                } else if (p.isMethod() == property.isMethod()) {
                    Element ownerP = p.getElement().getEnclosingElement();
                    Element ownerProperty = property.getElement().getEnclosingElement();
                    processingEnv.getMessager().printMessage(
                            Diagnostic.Kind.ERROR,
                            "Property conflict: "
                                    + (ownerP != null
                                    ? p.getElement().getSimpleName() + " in " + ownerP.getSimpleName()
                                    : p.getElement().getSimpleName())
                                    + " / "
                                    + (ownerProperty != null
                                    ? property.getElement().getSimpleName() + " in " + ownerProperty.getSimpleName()
                                    : property.getElement().getSimpleName())
                    );
                }
                done = true;
                break;
            }
        }
        if (!done && Utils.isNotObjectProperty(property)) {
            properties.add(property);
        }
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

    @Nonnull
    public String getPackageName() {
        return packageName;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public ProcessingEnvironment getProcessingEnv() {
        return processingEnv;
    }
}
