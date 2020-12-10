package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.annotations.*;
import io.github.vipcxj.beanknife.annotations.internal.GeneratedView;
import io.github.vipcxj.beanknife.utils.Utils;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ViewContext extends Context {

    private final ViewOfData viewOf;
    private final Type targetType;
    private final Type configType;
    private final Type genType;
    private final Type generatedType;
    private final boolean samePackage;
    private final List<String> errors;

    public ViewContext(@Nonnull ProcessingEnvironment processingEnv, @Nonnull ViewOfData viewOf) {
        super(processingEnv);
        this.viewOf = viewOf;
        this.targetType = Type.extract(viewOf.getTargetElement().asType());
        this.configType = Type.extract(viewOf.getConfigElement().asType());
        this.genType = Utils.extractGenType(
                this.targetType,
                viewOf.getGenName(),
                viewOf.getGenPackage(),
                "View"
        );
        this.generatedType = Type.extract(processingEnv.getElementUtils().getTypeElement(GeneratedView.class.getCanonicalName()).asType());
        this.packageName = this.genType.getPackageName();
        this.samePackage = this.targetType.isSamePackage(this.genType);
        this.containers.push(Type.fromPackage(this.packageName));
        this.errors = new ArrayList<>();
    }

    public ViewOfData getViewOf() {
        return viewOf;
    }

    public Type getTargetType() {
        return targetType;
    }

    public Type getGenType() {
        return genType;
    }

    public boolean isSamePackage() {
        return samePackage;
    }

    private int checkAnnConflict(NewViewProperty newViewProperty, OverrideViewProperty overrideViewProperty) {
        return (newViewProperty != null ? 1 : 0)
                + (overrideViewProperty != null ? 1 : 0);
    }

    public void error(String message) {
        errors.add(message);
        Utils.logWarn(getProcessingEnv(), message);
    }

    public void collectData() {
        TypeElement targetElement = viewOf.getTargetElement();
        TypeElement configElement = viewOf.getConfigElement();
        getProperties().clear();
        importVariable(this.targetType);
        importVariable(this.configType);
        importVariable(this.generatedType);
        Elements elementUtils = getProcessingEnv().getElementUtils();
        List<? extends Element> members = elementUtils.getAllMembers(targetElement);
        for (Element member : members) {
            Property property = null;
            if (member.getKind() == ElementKind.FIELD) {
                property = Utils.createPropertyFromBase(this, viewOf, (VariableElement) member, members, samePackage);
            } else if (member.getKind() == ElementKind.METHOD) {
                property = Utils.createPropertyFromBase(this, viewOf, (ExecutableElement) member, members, samePackage);
            }
            if (property != null) {
                addProperty(property, false);
            }
        }
        List<Pattern> includePatterns = Arrays.stream(viewOf.getIncludePattern().split(",\\s")).map(Pattern::compile).collect(Collectors.toList());
        List<Pattern> excludePatterns = Arrays.stream(viewOf.getExcludePattern().split(",\\s")).map(Pattern::compile).collect(Collectors.toList());
        getProperties().removeIf(property -> !Utils.canSeeFromOtherClass (property, samePackage)
                || (includePatterns.stream().noneMatch(pattern -> pattern.matcher(property.getName()).matches())
                && Arrays.stream(viewOf.getIncludes()).noneMatch(include -> include.equals(property.getName())))
                || excludePatterns.stream().anyMatch(pattern -> pattern.matcher(property.getName()).matches())
                || Arrays.stream(viewOf.getExcludes()).anyMatch(exclude -> exclude.equals(property.getName())));
        List<Property> baseProperties = new ArrayList<>(getProperties());
        if (configElement != targetElement) {
            members = elementUtils.getAllMembers(configElement);
            for (Element member : members) {
                NewViewProperty newViewProperty = member.getAnnotation(NewViewProperty.class);
                OverrideViewProperty overrideViewProperty = member.getAnnotation(OverrideViewProperty.class);
                int exists = checkAnnConflict(newViewProperty, overrideViewProperty);
                if (exists > 1) {
                    error("NewViewProperty and OverrideViewProperty should not be put on the same property.");
                    continue;
                }
                if (exists == 0) {
                    continue;
                }
                if (newViewProperty != null) {
                    if (baseProperties.stream().anyMatch(p -> p.getName().equals(newViewProperty.value()))) {
                        error("The property " + newViewProperty.value() + " already exists, so the @NewViewProperty annotation is invalid and has been ignored.");
                        continue;
                    }
                }
                if (overrideViewProperty != null) {
                    if (baseProperties.stream().noneMatch(p -> p.getName().equals(overrideViewProperty.value()))) {
                        error("The property " + overrideViewProperty.value() + " does not exists, so the @OverrideViewProperty annotation is invalid and has been ignored.");
                        continue;
                    }
                }
                if (member.getKind() == ElementKind.FIELD) {
                    if (overrideViewProperty != null) {
                        String name = overrideViewProperty.value();
                        getProperties().replaceAll(p -> {
                            if (p.getName().equals(name)) {
                                return p
                                        .withGetterAccess(Utils.resolveGetterAccess(viewOf, overrideViewProperty.getter()))
                                        .withSetterAccess(Utils.resolveSetterAccess(viewOf, overrideViewProperty.setter()));
                            } else {
                                return p;
                            }
                        });
                    } else {
                        throw new IllegalStateException("This is impossible!");
                    }
                } else if (member.getKind() == ElementKind.METHOD) {
                    Extractor extractor;
                    Type containerType = Type.extract(configElement.asType());
                    Dynamic dynamic = member.getAnnotation(Dynamic.class);
                    if (dynamic != null) {
                        extractor = new DynamicMethodExtractor(containerType, (ExecutableElement) member);
                    } else {
                        extractor = new StaticMethodExtractor(containerType, (ExecutableElement) member);
                    }
                    if (overrideViewProperty != null) {
                        String name = overrideViewProperty.value();
                        getProperties().replaceAll(p -> {
                            if (p.getName().equals(name)) {
                                extractor.check(this, p);
                                return p
                                        .withGetterAccess(Utils.resolveGetterAccess(viewOf, overrideViewProperty.getter()))
                                        .withSetterAccess(Utils.resolveSetterAccess(viewOf, overrideViewProperty.setter()))
                                        .withExtractor(extractor);
                            } else {
                                return p;
                            }
                        });
                    } else if (newViewProperty != null) {
                        extractor.check(this, null);
                        String name = newViewProperty.value();
                        Type type = extractor.getReturnType();
                        Access getterAccess = Utils.resolveGetterAccess(viewOf, newViewProperty.getter());
                        Access setterAccess = Utils.resolveSetterAccess(viewOf, newViewProperty.setter());
                        Modifier modifier = getterAccess.toModifier();
                        Property property = new Property(
                                newViewProperty.value(),
                                modifier,
                                getterAccess,
                                setterAccess,
                                type,
                                true,
                                Utils.createGetterName(name, type.isBoolean()),
                                Utils.createSetterName(name, type.isBoolean()),
                                false,
                                member,
                                getProcessingEnv().getElementUtils().getDocComment(member)
                        ).withExtractor(extractor);
                        addProperty(property, true);
                    }
                }
            }
        }
        for (Property property : getProperties()) {
            importVariable(property.getType());
        }
    }

    @Override
    public boolean print(@Nonnull PrintWriter writer) {
        Modifier modifier = viewOf.getAccess();
        if (modifier == null) {
            return false;
        }
        if (super.print(writer)) {
            writer.println();
        }
        writer.print("@");
        generatedType.printType(writer, this, false, false);
        writer.print("(targetClass = ");
        targetType.printType(writer, this, false, false);
        writer.print(".class, configClass = ");
        configType.printType(writer, this, false, false);
        writer.println(".class)");
        List<Property> properties = getProperties();
        boolean empty = true;
        enter(genType);
        genType.openClass(writer, modifier, this, INDENT, 0);
        for (Property property : properties) {
            if (!property.isDynamic()) {
                if (empty) {
                    empty = false;
                    writer.println();
                }
                property.printField(writer, this, INDENT, 1);
                writer.println();
            }
        }
        boolean hasEmptyConstructor = false;
        boolean hasFieldsConstructor = false;
        modifier = viewOf.getEmptyConstructor();
        if (modifier != null) {
            hasEmptyConstructor = true;
            if (empty) {
                empty = false;
                writer.println();
            }
            genType.emptyConstructor(writer, modifier, INDENT, 1);
            writer.println();
        }
        modifier = viewOf.getFieldsConstructor();
        if (modifier != null && !properties.isEmpty()) {
            hasFieldsConstructor = true;
            if (empty) {
                empty = false;
                writer.println();
            }
            genType.fieldsConstructor(writer, this, modifier, properties, INDENT, 1);
            writer.println();
        }
        modifier = viewOf.getCopyConstructor();
        if (modifier != null) {
            if (empty) {
                empty = false;
                writer.println();
            }
            genType.copyConstructor(writer, this, modifier, properties, INDENT, 1);
            writer.println();
        }
        empty = printReader(writer, empty, hasEmptyConstructor, hasFieldsConstructor);
        for (Property property : properties) {
            if (property.hasGetter()) {
                if (empty) {
                    empty = false;
                    writer.println();
                }
                property.printGetter(writer, this, INDENT, 1);
                writer.println();
            }
        }
        for (Property property : properties) {
            if (!property.isDynamic() && property.hasSetter()) {
                if (empty) {
                    empty = false;
                    writer.println();
                }
                property.printSetter(writer, this, INDENT, 1);
                writer.println();
            }
        }
        printErrors(writer, empty);
        exit();
        genType.closeClass(writer, INDENT, 0);
        return true;
    }

    private void printErrors(@Nonnull PrintWriter writer, boolean empty) {
        if (!viewOf.isErrorMethods()) {
            return;
        }
        if (empty) {
            writer.println();
        }
        int i = 0;
        for (String error : errors) {
            Utils.printIndent(writer, INDENT, 1);
            writer.print("public static void ");
            writer.print("error");
            writer.print(i++);
            writer.println("() {");
            Utils.printIndent(writer, INDENT, 2);
            writer.print("return ");
            writer.print("\"");
            writer.print(StringEscapeUtils.escapeJava(error));
            writer.print("\";");
            Utils.printIndent(writer, INDENT, 1);
            writer.println("}");
            writer.println();
        }
    }

    private boolean printReader(
            @Nonnull PrintWriter writer,
            boolean empty,
            boolean hasEmptyConstructor,
            boolean hasFieldsConstructor
    ) {
        Modifier modifier = viewOf.getReadMethod();
        if (modifier == null) {
            return empty;
        }
        if (empty) {
            writer.println();
        }
        List<Property> properties = getProperties();
        Utils.printIndent(writer, INDENT, 1);
        Utils.printModifier(writer, modifier);
        writer.print("static ");
        if (!genType.getParameters().isEmpty()) {
            genType.printGenericParameters(writer, this, true);
            writer.print(" ");
        }
        genType.printType(writer, this, true, false);
        writer.print(" read(");
        targetType.printType(writer, this, true, false);
        writer.println(" source) {");
        if (hasEmptyConstructor) {
            Utils.printIndent(writer, INDENT, 2);
            genType.printType(writer, this, true, false);
            writer.print(" out = new ");
            writer.print(genType.getSimpleName());
            if (!genType.getParameters().isEmpty()) {
                writer.print("<>");
            }
            writer.println("();");
            for (Property property : properties) {
                if (!property.isDynamic()) {
                    Utils.printIndent(writer, INDENT, 2);
                    writer.print("out.");
                    writer.print(this.getMappedFieldName(property));
                    writer.print(" = ");
                    if (property.isCustomMethod()) {
                        property.getExtractor().print(writer, this);
                        writer.println(";");
                    } else {
                        if (property.isMethod()) {
                            writer.print("source.");
                            writer.print(property.getGetterName());
                            writer.println("();");
                        } else {
                            writer.print("source.");
                            writer.print(property.getName());
                            writer.println(";");
                        }
                    }
                }
            }
            Utils.printIndent(writer, INDENT, 2);
            writer.println("return out;");
        } else if (hasFieldsConstructor) {
            Utils.printIndent(writer, INDENT, 2);
            writer.print("return new ");
            writer.print(genType.getSimpleName());
            if (!genType.getParameters().isEmpty()) {
                writer.print("<>");
            }
            writer.println("(");
            int i = 0;
            properties = properties.stream().filter(p -> !p.isDynamic()).collect(Collectors.toList());
            for (Property property : properties) {
                Utils.printIndent(writer, INDENT, 3);
                if (property.isCustomMethod()) {
                    property.getExtractor().print(writer, this);
                } else {
                    if (property.isMethod()) {
                        writer.print("source.");
                        writer.print(property.getGetterName());
                        writer.print("()");
                    } else {
                        writer.print("source.");
                        writer.print(property.getName());
                        writer.print("");
                    }
                }
                if (i != properties.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
                ++i;
            }
            Utils.printIndent(writer, INDENT, 2);
            writer.println(");");
        }
        Utils.printIndent(writer, INDENT, 1);
        writer.println("}");
        writer.println();
        return false;
    }
}
