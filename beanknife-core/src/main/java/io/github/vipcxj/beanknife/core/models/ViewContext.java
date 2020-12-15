package io.github.vipcxj.beanknife.core.models;

import com.sun.source.util.Trees;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.runtime.PropertyConverter;
import io.github.vipcxj.beanknife.runtime.annotations.*;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.*;
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

    public ViewContext(@NonNull Trees trees, @NonNull ProcessingEnvironment processingEnv, @NonNull ProcessorData processorData, @NonNull ViewOfData viewOf) {
        super(trees, processingEnv, processorData);
        this.viewOf = viewOf;
        this.targetType = Type.extract(this, viewOf.getTargetElement().asType());
        this.configType = Type.extract(this, viewOf.getConfigElement().asType());
        this.genType = Utils.extractGenType(
                this.targetType,
                viewOf.getGenName(),
                viewOf.getGenPackage(),
                "View"
        );
        this.generatedType = Type.extract(this, GeneratedView.class);
        this.packageName = this.genType.getPackageName();
        this.samePackage = this.targetType.isSamePackage(this.genType);
        this.containers.push(Type.fromPackage(this, this.packageName));
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

    public Trees getTrees() {
        return trees;
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
                        List<DeclaredType> converters = getConverters(member);
                        String name = overrideViewProperty.value();
                        Type newType = extractType(member);
                        getProperties().replaceAll(p -> {
                            if (p.getName().equals(name)) {
                                Property newProperty = p
                                        .withGetterAccess(Utils.resolveGetterAccess(viewOf, overrideViewProperty.getter()))
                                        .withSetterAccess(Utils.resolveSetterAccess(viewOf, overrideViewProperty.setter()));
                                DeclaredType converter = selectConverter(member, converters, member.asType(), p.getTypeMirror());
                                // DeclaredType viewTarget = getViewTarget(newType);
                                // boolean isView = false;
                                if (!getProcessingEnv().getTypeUtils().isAssignable(p.getTypeMirror(), null) && converter == null) {
                                    // if (viewTarget == null) {
                                        error("The property " + p.getName() + " can not be override because type mismatched.");
                                        return null;
                                    // }
//                                    if (!getProcessingEnv().getTypeUtils().isSameType(viewTarget, p.getTypeMirror())) {
//                                        error("The property \" + p.getName() + \" can not be override, because it is not the view type of " + p.getType().getQualifiedName() + ".");
//                                        return null;
//                                    }
//                                    isView = true;
                                }
                                // newProperty = newProperty.withType(newType, isView ? viewTarget : null);
                                newProperty = newProperty.withType(null, null);
                                return converter != null ? newProperty.withConverter(converter) : newProperty;
                            } else {
                                return p;
                            }
                        });
                        getProperties().removeIf(Objects::isNull);
                    } else {
                        throw new IllegalStateException("This is impossible!");
                    }
                } else if (member.getKind() == ElementKind.METHOD) {
                    Extractor extractor;
                    Type containerType = Type.extract(this, configElement.asType());
                    Type newType = extractType(member);
                    Dynamic dynamic = member.getAnnotation(Dynamic.class);
                    if (dynamic != null) {
                        extractor = new DynamicMethodExtractor(containerType, (ExecutableElement) member, null);
                    } else {
                        extractor = new StaticMethodExtractor(containerType, (ExecutableElement) member, null);
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
                        Modifier modifier = Utils.accessToModifier(getterAccess);
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
            DeclaredType converter = property.getConverter();
            if (converter != null) {
                importVariable(Type.extract(this, converter));
            }
        }
    }

    private DeclaredType getViewTarget(TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            TypeElement typeElement = Utils.toElement(declaredType);
            if (typeElement.getKind() == ElementKind.CLASS) {
                for (AnnotationMirror annotationMirror : typeElement.getAnnotationMirrors()) {
                    if (Utils.isThisAnnotation(annotationMirror, GeneratedView.class)) {
                        Map<? extends ExecutableElement, ? extends AnnotationValue> attributes = getProcessingEnv().getElementUtils().getElementValuesWithDefaults(annotationMirror);
                        return Utils.getTypeAnnotationValue(annotationMirror, attributes, "targetClass");
                    }
                }
                return null;
            } else if (typeElement.getKind() == ElementKind.INTERFACE) {
                if (
                        Utils.isThisTypeElement(typeElement, List.class)
                        || Utils.isThisTypeElement(typeElement, Queue.class)
                        || Utils.isThisTypeElement(typeElement, Set.class)
                ) {
                    return getViewTarget(declaredType.getTypeArguments().get(0));
                } else if (Utils.isThisTypeElement(typeElement, Map.class)) {
                    return getViewTarget(declaredType.getTypeArguments().get(1));
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } else if (typeMirror.getKind() == TypeKind.ARRAY) {
            return getViewTarget(((ArrayType) typeMirror).getComponentType());
        } else {
            return null;
        }
    }

    private List<DeclaredType> getConverters(Element element) {
        return Utils.getAnnotationsOn(this, element, UsePropertyConverter.class, UsePropertyConverters.class)
                .stream().map(annotation -> {
                    Map<? extends ExecutableElement, ? extends AnnotationValue> attributes = getProcessingEnv().getElementUtils().getElementValuesWithDefaults(annotation);
                    return Utils.getTypeAnnotationValue(annotation, attributes, "value");
                }).collect(Collectors.toList());
    }

    private int calcTypeAssignScore(DeclaredType beAssigned, DeclaredType toAssign) {
        if (getProcessingEnv().getTypeUtils().isSameType(beAssigned, toAssign)) {
            return 0;
        }
        TypeElement beAssignedElement = (TypeElement) beAssigned.asElement();
        TypeElement toAssignElement = (TypeElement) toAssign.asElement();
        boolean beAssignedIsInterface = beAssignedElement.getKind() == ElementKind.INTERFACE;
        boolean beAssignedIsAnnotation = Utils.isThisTypeElement(beAssignedElement, Annotation.class);
        boolean beAssignedIsEnum = Utils.isThisTypeElement(beAssignedElement, Enum.class);
        boolean toAssignIsAnnotation = toAssignElement.getKind() == ElementKind.ANNOTATION_TYPE;
        boolean toAssignIsClass = toAssignElement.getKind() == ElementKind.CLASS;
        boolean toAssignIsEnum = toAssignElement.getKind() == ElementKind.ENUM;
        int score = -1;
        if (
                beAssignedElement.getKind() == toAssignElement.getKind()
                || (beAssignedIsInterface && toAssignIsClass)
                || (beAssignedIsAnnotation && toAssignIsAnnotation)
                || (beAssignedIsEnum && toAssignIsEnum)
        ) {
            if (beAssignedIsInterface) {
                for (TypeMirror anInterface : toAssignElement.getInterfaces()) {
                    int parentScore = calcTypeAssignScore(beAssigned, (DeclaredType) anInterface);
                    if (parentScore >= 0 && (score == -1 || score > parentScore + 1)) {
                        score = parentScore + 1;
                    }
                }
            }
            if (beAssignedIsAnnotation && toAssignIsAnnotation) {
                score = 1;
            }
            if (toAssignIsClass) {
                TypeMirror superClass = toAssignElement.getSuperclass();
                if (superClass.getKind() != TypeKind.NONE) {
                    int parentScore = calcTypeAssignScore(beAssigned, (DeclaredType) superClass);
                    if (parentScore >= 0 && (score == -1 || score > parentScore + 1)) {
                        score = parentScore + 1;
                    }
                }
            }
            if (beAssignedIsEnum && toAssignIsEnum) {
                score = 1;
            }
        }
        return score;
    }

    // let beAssigned := toAssign
    private int calcTypeAssignScore(TypeMirror beAssigned, TypeMirror toAssign) {
        Types typeUtils = getProcessingEnv().getTypeUtils();
        if (!typeUtils.isAssignable(toAssign, beAssigned)) {
            return -1;
        }
        if (beAssigned.getKind() == TypeKind.ARRAY) {
            ArrayType beAssignedArrayType = (ArrayType) beAssigned;
            ArrayType toAssignArrayType = (ArrayType) toAssign;
            return calcTypeAssignScore(beAssignedArrayType.getComponentType(), toAssignArrayType.getComponentType());
        } else if (beAssigned.getKind().isPrimitive()) {
            return typeUtils.isSameType(beAssigned, toAssign) ? 0 : 1;
        } else if (beAssigned.getKind() == TypeKind.DECLARED) {
            return calcTypeAssignScore((DeclaredType) beAssigned, (DeclaredType) toAssign);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private int calcConverterScore(DeclaredType converter, TypeMirror toType, TypeMirror fromType) {
        Types typeUtils = getProcessingEnv().getTypeUtils();
        DeclaredType realConverter = Utils.findSuperType(converter, PropertyConverter.class);
        if (realConverter == null) {
            throw new IllegalArgumentException("This is impossible.");
        }
        List<? extends TypeMirror> typeArguments = realConverter.getTypeArguments();
        TypeMirror converterFromType = typeArguments.get(0);
        TypeMirror converterToType = typeArguments.get(1);
        if (!typeUtils.isAssignable(fromType, converterFromType) || !typeUtils.isAssignable(converterToType, toType)) {
            return -1;
        }
        return calcTypeAssignScore(converterFromType, fromType) + calcTypeAssignScore(toType, converterToType);
    }

    private DeclaredType selectConverter(Element member, List<DeclaredType> converters, TypeMirror toType, TypeMirror fromType) {
        List<DeclaredType> results = new ArrayList<>();
        int score = -1;
        for (DeclaredType converter : converters) {
            TypeElement converterElement = Utils.toElement(converter);
            if (!converterElement.getTypeParameters().isEmpty()) {
                error("The property converter must make sure the from type and to type. So it can not be a generic type. The invalid converter: " + converterElement.getQualifiedName() + ".");
                continue;
            }
            if (converterElement.getModifiers().contains(Modifier.ABSTRACT)) {
                error("The property converter should be instantiable. So it can not be abstract. The invalid converter: " + converterElement.getQualifiedName() + ".");
                continue;
            }
            if (!Utils.hasEmptyConstructor(getProcessingEnv(), converterElement)) {
                error("The property converter should be instantiable. So it should has a empty constructor. The invalid converter: " + converterElement.getQualifiedName() + ".");
                continue;
            }
            int theScore = calcConverterScore(converter, toType, fromType);
            if (theScore >= 0 && (score == -1 || score >= theScore)) {
                score = theScore;
                results.clear();
                results.add(converter);
            }
        }
        if (results.size() == 1) {
            return results.get(0);
        } else if (results.size() > 1) {
            error(
                    "Ambiguous converters on member " +
                            member.getSimpleName() +
                            ": " +
                            results.stream()
                                    .map(converter -> ((TypeElement) converter.asElement()).getQualifiedName())
                                    .collect(Collectors.joining(", "))
            );
            return null;
        } else {
            return null;
        }
    }

    @Override
    public boolean print(@NonNull PrintWriter writer) {
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

    private void printErrors(@NonNull PrintWriter writer, boolean empty) {
        if (!viewOf.isErrorMethods()) {
            return;
        }
        if (empty) {
            writer.println();
        }
        int i = 0;
        for (String error : errors) {
            Utils.printIndent(writer, INDENT, 1);
            writer.print("public static String ");
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
            @NonNull PrintWriter writer,
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
                        DeclaredType converter = property.getConverter();
                        DeclaredType viewTarget = property.getViewTarget();
                        if (converter != null) {
                            writer.print("new ");
                            Type.extract(this, converter).printType(writer, this, false, false);
                            writer.print("().convert(");
                        } else if (viewTarget != null) {
                            property.getType().printType(writer, this, false, false);
                            writer.print(".read(");
                        }
                        if (property.isMethod()) {
                            writer.print("source.");
                            writer.print(property.getGetterName());
                            writer.print("()");
                        } else {
                            writer.print("source.");
                            writer.print(property.getName());
                        }
                        if (converter != null || viewTarget != null) {
                            writer.println(");");
                        } else {
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
