package io.github.vipcxj.beanknife.core.models;

import com.sun.source.util.Trees;
import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.ViewCodeGenerators;
import io.github.vipcxj.beanknife.core.spi.ViewCodeGenerator;
import io.github.vipcxj.beanknife.core.utils.*;
import io.github.vipcxj.beanknife.runtime.BeanProviders;
import io.github.vipcxj.beanknife.runtime.PropertyConverter;
import io.github.vipcxj.beanknife.runtime.annotations.*;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import io.github.vipcxj.beanknife.runtime.utils.AnnotationDest;
import io.github.vipcxj.beanknife.runtime.utils.BeanUsage;
import io.github.vipcxj.beanknife.runtime.utils.CacheType;
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
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class ViewContext extends Context {

    public final static String READ_CONFIG_BEAN_VAR = "configureBean";
    private final static String TYPE_BEAN_PROVIDERS = "io.github.vipcxj.beanknife.runtime.BeanProviders";
    private final static String SIMPLE_TYPE_BEAN_PROVIDERS = "BeanProviders";
    private final static String TYPE_BEAN_USAGE = "io.github.vipcxj.beanknife.runtime.utils.BeanUsage";
    private final static String SIMPLE_TYPE_BEAN_USAGE = "BeanUsage";
    private final ViewOfData viewOf;
    private final Type targetType;
    private final Type configType;
    private final Type genType;
    private final Type generatedType;
    private final boolean samePackage;
    private List<Property> baseProperties;
    private List<Property> extraProperties;
    private final Map<String, ParamInfo> extraParams;
    private boolean useConfigureBeanVarInRead;
    private boolean useCachedConfigureBeanField;

    public ViewContext(@NonNull Trees trees, @NonNull ProcessingEnvironment processingEnv, @NonNull ProcessorData processorData, @NonNull ViewOfData viewOf) {
        super(trees, processingEnv, processorData);
        this.viewOf = viewOf;
        this.targetType = Type.extract(this, viewOf.getTargetElement());
        this.configType = Type.extract(this, viewOf.getConfigElement());
        this.genType = Utils.extractGenType(
                this.targetType,
                viewOf.getGenName(),
                viewOf.getGenPackage(),
                "View"
        );
        this.generatedType = Type.extract(this, GeneratedView.class);
        this.packageName = this.genType.getPackageName();
        this.samePackage = this.targetType.isSamePackage(this.genType);
        this.extraProperties = new ArrayList<>();
        this.extraParams = new TreeMap<>();
        this.containers.push(Type.fromPackage(this, this.packageName));
        this.useCachedConfigureBeanField = false;
        this.useConfigureBeanVarInRead = false;
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

    public Type getConfigType() {
        return configType;
    }

    public Trees getTrees() {
        return trees;
    }

    public List<Property> getBaseProperties() {
        return baseProperties;
    }

    public List<Property> getExtraProperties() {
        return extraProperties;
    }

    public boolean hasExtraProperties() {
        return !extraProperties.isEmpty();
    }

    public Map<String, ParamInfo> getExtraParams() {
        return extraParams;
    }

    public boolean hasExtraParams() {
        return !extraParams.isEmpty();
    }

    public boolean hasFieldsConstructor() {
        return viewOf.getFieldsConstructor() != null;
    }

    public boolean canSeeReadConstructor(@NonNull String fromPackage) {
        boolean samePackage = fromPackage.equals(genType.getPackageName());
        if (samePackage) {
            return viewOf.getReadConstructor() != null && viewOf.getReadConstructor() != Modifier.PRIVATE;
        } else {
            return viewOf.getReadConstructor() == Modifier.PUBLIC;
        }
    }

    public boolean canSeeFieldsConstructor(@NonNull String fromPackage) {
        boolean samePackage = fromPackage.equals(genType.getPackageName());
        if (samePackage) {
            return viewOf.getFieldsConstructor() != null && viewOf.getFieldsConstructor() != Modifier.PRIVATE;
        } else {
            return viewOf.getFieldsConstructor() == Modifier.PUBLIC;
        }
    }

    public boolean canSeeEmptyConstructor(@NonNull String fromPackage) {
        boolean samePackage = fromPackage.equals(genType.getPackageName());
        if (samePackage) {
            return viewOf.getEmptyConstructor() != null && viewOf.getEmptyConstructor() != Modifier.PRIVATE;
        } else {
            return viewOf.getFieldsConstructor() == Modifier.PUBLIC;
        }
    }

    private int checkAnnConflict(
            @CheckForNull NewViewProperty newViewProperty,
            @CheckForNull OverrideViewProperty overrideViewProperty,
            @CheckForNull MapViewProperty mapViewProperty
    ) {
        return (newViewProperty != null ? 1 : 0)
                + (overrideViewProperty != null ? 1 : 0)
                + (mapViewProperty != null ? 1 : 0);
    }

    public void collectData() {
        System.out.println("collecting data...");
        TypeElement targetElement = viewOf.getTargetElement();
        TypeElement configElement = viewOf.getConfigElement();
        getProperties().clear();
        importVariable(this.targetType);
        importVariable(this.configType);
        importVariable(this.generatedType);
        importVariable(Type.extract(this, List.class));
        importVariable(Type.extract(this, ArrayList.class));
        importVariable(Type.extract(this, Set.class));
        importVariable(Type.extract(this, HashSet.class));
        importVariable(Type.extract(this, Stack.class));
        importVariable(Type.extract(this, Map.class));
        importVariable(Type.extract(this, HashMap.class));
        if (viewOf.isSerializable()) {
            importVariable(Type.extract(this, Serializable.class));
        }
        if (viewOf.getCreateAndWriteBackMethod() != Access.NONE) {
            importVariable(Type.extract(this, BeanProviders.class));
            importVariable(Type.extract(this, BeanUsage.class));
        }
        for (AnnotationMirror annotationMirror : viewOf.getAnnotationMirrors()) {
            Utils.importAnnotation(this, annotationMirror);
        }
        Elements elementUtils = getProcessingEnv().getElementUtils();
        final List<? extends Element> targetMembers = ElementsCompatible.getAllMembers(elementUtils, targetElement);
        Access typeGetterAccess = LombokUtils.getGetterAccess(targetElement, null);
        Access typeSetterAccess = LombokUtils.getSetterAccess(targetElement, null);
        for (Element member : targetMembers) {
            if (member.getModifiers().contains(Modifier.STATIC)) {
                continue;
            }
            Property property = null;
            if (member.getKind() == ElementKind.FIELD) {
                property = Utils.createPropertyFromBase(this, viewOf, (VariableElement) member, typeGetterAccess, typeSetterAccess);
            } else if (member.getKind() == ElementKind.METHOD) {
                property = Utils.createPropertyFromBase(this, viewOf, (ExecutableElement) member);
            }
            if (property != null) {
                addProperty(property, false);
            }
        }
        getProperties().removeIf(property -> Utils.canNotSeeFromOtherClass(property, samePackage));
        getProperties().replaceAll(property -> {
            Property base = property.getBase();
            if (base != null) {
                ExecutableElement setterMethod = Utils.getSetterMethod(processingEnv, base.getSetterName(), base.getTypeMirror(), targetMembers);
                if (setterMethod != null) {
                    boolean writeable = Utils.canSeeFromOtherClass(setterMethod, samePackage);
                    return property.withWriteInfo(writeable, true);
                } else {
                    Property field = property.getField();
                    if (field != null) {
                        if (field.isLombokWritable(samePackage)) {
                            return property.withWriteInfo(true, true);
                        } else {
                            return property.withWriteInfo(Utils.canSeeFromOtherClass(field.getElement(), samePackage), false);
                        }
                    } else {
                        return property;
                    }
                }
            } else {
                return property;
            }
        });
        this.baseProperties = new ArrayList<>(getProperties());
        List<Pattern> includePatterns = new ArrayList<>();
        for (String p : viewOf.getIncludePattern().split(",\\s*|\\s+")) {
            try {
                Pattern pattern = Pattern.compile(p);
                includePatterns.add(pattern);
            } catch (PatternSyntaxException e) {
                error("Invalid include pattern part: \"" + p + "\"." + System.lineSeparator() +
                        e.getMessage() + System.lineSeparator() +
                        "Include pattern is a space or comma divided string which each part is a valid regex pattern. " +
                        "For example: \"[aA]pple, [oO]range\" matches apple, Apple, orange and Orange."
                );
            }
        }
        List<Pattern> excludePatterns = new ArrayList<>();
        for (String p : viewOf.getExcludePattern().split(",\\s*|\\s+")) {
            try {
                Pattern pattern = Pattern.compile(p);
                excludePatterns.add(pattern);
            } catch (PatternSyntaxException e) {
                error("Invalid exclude pattern part: \"" + p + "\"." + System.lineSeparator() +
                        e.getMessage() + System.lineSeparator() +
                        "Exclude pattern is a space or comma divided string which each part is a valid regex pattern. " +
                        "For example: \"[aA]pple, [oO]range\" matches apple, Apple, orange and Orange."
                );
            }
        }
        getProperties().removeIf(
                property -> (includePatterns.stream().noneMatch(pattern -> pattern.matcher(property.getName()).matches())
                        && Arrays.stream(viewOf.getIncludes()).noneMatch(include -> include.equals(property.getName())))
                        || excludePatterns.stream().anyMatch(pattern -> pattern.matcher(property.getName()).matches())
                        || Arrays.stream(viewOf.getExcludes()).anyMatch(exclude -> exclude.equals(property.getName()))
                        || viewOf.getExtraExcludes().contains(property.getName())
        );
        List<Property> baseProperties = new ArrayList<>(getProperties());
        if (!Objects.equals(configElement, targetElement)) {
            List<? extends Element> configMembers = elementUtils.getAllMembers(configElement);
            for (Element member : configMembers) {
                NewViewProperty newViewProperty = member.getAnnotation(NewViewProperty.class);
                OverrideViewProperty overrideViewProperty = member.getAnnotation(OverrideViewProperty.class);
                MapViewProperty mapViewProperty = member.getAnnotation(MapViewProperty.class);
                int exists = checkAnnConflict(newViewProperty, overrideViewProperty, mapViewProperty);
                if (exists > 1) {
                    error("NewViewProperty, OverrideViewProperty and MapViewProperty should not be put on the same property.");
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
                if (mapViewProperty != null) {
                    if (baseProperties.stream().noneMatch(p -> p.getName().equals(mapViewProperty.map()))) {
                        error("The property " + mapViewProperty.map() + " does not exists, so the @MapViewProperty annotation is invalid and has been ignored.");
                        continue;
                    }
                    if (!mapViewProperty.name().equals(mapViewProperty.map()) && getProperties().stream().anyMatch(p -> p.getName().equals(mapViewProperty.name()))) {
                        error("The property " + mapViewProperty.name() + " already exists, so the @MapViewProperty annotation is invalid and has been ignored.");
                        continue;
                    }
                }
                if (member.getKind() == ElementKind.FIELD) {
                    if (overrideViewProperty != null || mapViewProperty != null) {
                        List<DeclaredType> converters = getConverters(member);
                        String name = overrideViewProperty != null ? overrideViewProperty.value() : mapViewProperty.name();
                        String mappedName = overrideViewProperty != null ? overrideViewProperty.value() : mapViewProperty.map();
                        Type newType = Type.extract(this, member);
                        if (newType == null) {
                            error("Unable to resolve the type of field property: " + member.getSimpleName() + ".");
                            continue;
                        }
                        getProperties().replaceAll(p -> {
                            if (p.getName().equals(mappedName)) {
                                Access getterAccess = overrideViewProperty != null ? overrideViewProperty.getter() : mapViewProperty.getter();
                                Access setterAccess = overrideViewProperty != null ? overrideViewProperty.setter() : mapViewProperty.setter();
                                Property newProperty = p.extend(member, name)
                                        .withGetterAccess(Utils.resolveGetterAccess(viewOf, getterAccess))
                                        .withSetterAccess(Utils.resolveSetterAccess(viewOf, setterAccess));
                                TypeElement converter = selectConverter(member, converters, member.asType(), p.getTypeMirror());
                                Type converterType = null;
                                if (converter != null) {
                                    converterType = Type.extract(this, converter);
                                    if (converterType == null) {
                                        error("The converter of the property " + p.getName() + " can not be resolved. So this property is ignored.");
                                        return null;
                                    }
                                }
                                boolean isView = matchViewType(p.getType(), newType);
                                if (!p.getType().canAssignTo(newType) && converter == null) {
                                    if (!isView) {
                                        error("The property " + p.getName() + " can not be override because type mismatched.");
                                        return null;
                                    } else {
                                        ViewContext viewContext = getViewContext(newType);
                                        if (viewContext == null) {
                                            throw new NullPointerException("This is impossible!");
                                        }
                                        if (!viewContext.isLocked()) {
                                            error("There exists  circular reference on the property " + p.getName() + ". This may cause the generated class not stable and produce unpredictable behavior.");
                                        }
                                        if (viewContext.hasExtraProperties() || viewContext.hasExtraParams()) {
                                            error("Unable to convert from " +
                                                    viewContext.getTargetType() +
                                                    " to its view type " +
                                                    viewContext.getGenType() +
                                                    ". Because it has extra properties or extra params."
                                            );
                                            return null;
                                        }
                                    }
                                }
                                newProperty = newProperty.withType(newType, isView);
                                return converterType != null ? newProperty.withConverter(converterType) : newProperty;
                            } else {
                                return p;
                            }
                        });
                        getProperties().removeIf(Objects::isNull);
                    } else if (newViewProperty != null) {
                        String name = newViewProperty.value();
                        Type type = Type.extract(this, member);
                        Access getterAccess = Utils.resolveGetterAccess(viewOf, newViewProperty.getter());
                        Access setterAccess = Utils.resolveSetterAccess(viewOf, newViewProperty.setter());
                        Modifier modifier = Utils.accessToModifier(getterAccess);
                        Property property = new Property(
                                newViewProperty.value(),
                                false,
                                modifier,
                                getterAccess,
                                setterAccess,
                                type,
                                false,
                                Utils.createGetterName(name, type.isBoolean()),
                                Utils.createSetterName(name, type.isBoolean()),
                                member,
                                getProcessingEnv().getElementUtils().getDocComment(member),
                                null
                        );
                        addProperty(property, true);
                    }
                } else if (member.getKind() == ElementKind.METHOD) {
                    Extractor extractor;
                    Type containerType = Type.extract(this, configElement);
                    if (containerType == null) {
                        error("Unable to resolve the type config element: " + configElement.getQualifiedName());
                        continue;
                    }
                    Dynamic dynamic = member.getAnnotation(Dynamic.class);
                    if (dynamic != null) {
                        extractor = new DynamicMethodExtractor(this, (ExecutableElement) member, genType);
                    } else {
                        extractor = new StaticMethodExtractor(this, (ExecutableElement) member);
                    }
                    if (overrideViewProperty != null || mapViewProperty != null) {
                        String name = overrideViewProperty != null ? overrideViewProperty.value() : mapViewProperty.name();
                        String mappedName = overrideViewProperty != null ? overrideViewProperty.value() : mapViewProperty.map();
                        getProperties().replaceAll(p -> {
                            if (p.getName().equals(mappedName)) {
                                Access getterAccess = overrideViewProperty != null ? overrideViewProperty.getter() : mapViewProperty.getter();
                                Access setterAccess = overrideViewProperty != null ? overrideViewProperty.setter() : mapViewProperty.setter();
                                return p.extend(member, name)
                                        .withGetterAccess(Utils.resolveGetterAccess(viewOf, getterAccess))
                                        .withSetterAccess(Utils.resolveSetterAccess(viewOf, setterAccess))
                                        .withExtractor(extractor);
                            } else {
                                return p;
                            }
                        });
                    } else if (newViewProperty != null) {
                        String name = newViewProperty.value();
                        Type type = extractor.getReturnType();
                        Access getterAccess = Utils.resolveGetterAccess(viewOf, newViewProperty.getter());
                        Access setterAccess = Utils.resolveSetterAccess(viewOf, newViewProperty.setter());
                        Modifier modifier = Utils.accessToModifier(getterAccess);
                        Property property = new Property(
                                newViewProperty.value(),
                                false,
                                modifier,
                                getterAccess,
                                setterAccess,
                                type,
                                true,
                                Utils.createGetterName(name, type.isBoolean()),
                                Utils.createSetterName(name, type.isBoolean()),
                                member,
                                getProcessingEnv().getElementUtils().getDocComment(member),
                                null
                        ).withExtractor(extractor);
                        addProperty(property, true);
                    }
                }
            }
        }
        getProperties().removeIf(property -> {
            Extractor extractor = property.getExtractor();
            return extractor != null && !extractor.check();
        });
        ListIterator<Property> propertyListIterator = getProperties().listIterator();
        while (propertyListIterator.hasNext()) {
            Property property = propertyListIterator.next();
            importVariable(property.getType());
            Extractor extractor = property.getExtractor();
            if (extractor != null && !extractor.isDynamic()) {
                List<ParamInfo> paramInfoList = ((StaticMethodExtractor) extractor).getParamInfoList();
                boolean conflict = false;
                for (ParamInfo paramInfo : paramInfoList) {
                    if (paramInfo.isExtraParam()) {
                        String extraParamName = paramInfo.getExtraParamName();
                        ParamInfo existParamInfo = extraParams.get(extraParamName);
                        if (existParamInfo == null) {
                            extraParams.put(extraParamName, paramInfo);
                        } else {
                            Type type = Type.extract(this, paramInfo.getVar());
                            Type existType = Type.extract(this, existParamInfo.getVar());
                            if (type == null) {
                                error("Unable to resolve the type of the argument \"" + paramInfo.getParameterName() + "\" in the static property method \"" + paramInfo.getMethodName() + "\".");
                                conflict = true;
                                break;
                            }
                            if (existType.canAssignTo(type)) {
                                extraParams.put(extraParamName, paramInfo);
                            } else if (!type.canAssignTo(existType)) {
                                error("The type of extra param \"" +
                                        extraParamName +
                                        "\" in the static property method \"" +
                                        paramInfo.getMethodName() +
                                        "\" conflict with the extra param with the same name in the static property method \"" +
                                        existParamInfo.getMethodName() +
                                        "."
                                );
                                conflict = true;
                                break;
                            }
                        }
                    }
                }
                if (conflict) {
                    propertyListIterator.remove();
                    continue;
                }
            }
            if (extractor != null && extractor.useBeanProvider()) {
                importVariable(TYPE_BEAN_PROVIDERS, SIMPLE_TYPE_BEAN_PROVIDERS);
                importVariable(TYPE_BEAN_USAGE, SIMPLE_TYPE_BEAN_USAGE);
                if (extractor.isDynamic() && viewOf.getConfigureBeanCacheType() == CacheType.LOCAL) {
                    useCachedConfigureBeanField = true;
                }
                if (!extractor.isDynamic()) {
                    useConfigureBeanVarInRead = true;
                }
            }
            Type converter = property.getConverter();
            if (converter != null) {
                importVariable(converter);
            }
            List<AnnotationMirror> annotationMirrors = property.collectAnnotations(this, AnnotationDest.FIELD);
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                Utils.importAnnotation(this, annotationMirror);
            }
            annotationMirrors = property.collectAnnotations(this, AnnotationDest.GETTER);
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                Utils.importAnnotation(this, annotationMirror);
            }
            annotationMirrors = property.collectAnnotations(this, AnnotationDest.SETTER);
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                Utils.importAnnotation(this, annotationMirror);
            }
        }
        extraProperties = getProperties()
                .stream()
                .filter(p -> !p.isDynamic() && !p.isCustomMethod() && p.getBase() == null)
                .collect(Collectors.toList());

        System.out.println("ready generators...");
        for (ViewCodeGenerator generator : ViewCodeGenerators.INSTANCE.getGenerators()) {
            System.out.println("Find generator " + generator.getClass().getName() + ".");
            generator.ready(this);
        }
        System.out.println("All generators have been ready.");

        lock();
    }

    @CheckForNull
    public ViewContext getViewContext(@NonNull Type type) {
        if (isViewType(type)) {
            return processorData.getViewContext(getViewData(type));
        }
        if ((type.isType(List.class))
                || type.isType(Stack.class)
                || type.isType(Set.class)) {
            return getViewContext(type.getParameters().get(0));
        } else if (type.isType(Map.class)) {
            return getViewContext(type.getParameters().get(1));
        } else if (type.isArray()) {
            return getViewContext(Objects.requireNonNull(type.getComponentType()));
        } else {
            return null;
        }
    }

    private boolean matchViewType(Type sourceType, Type targetType) {
        if (isViewType(targetType)) {
            ViewOfData viewData = getViewData(targetType);
            Type type = Type.extract(this, viewData.getTargetElement());
            if (type == null) {
                error("Unable to resolve the type: " + viewData.getTargetElement().getQualifiedName() + ".");
                return false;
            }
            return sourceType.sameType(type);
        }
        if ((sourceType.isType(List.class) && targetType.isType(List.class))
                || (sourceType.isType(Stack.class) && targetType.isType(Stack.class))
                || (sourceType.isType(Set.class) && targetType.isType(Set.class))) {
            return matchViewType(sourceType.getParameters().get(0), targetType.getParameters().get(0));
        } else if (sourceType.isType(Map.class) && targetType.isType(Map.class)) {
            return matchViewType(sourceType.getParameters().get(1), targetType.getParameters().get(1));
        } else if (sourceType.isArray() && targetType.isArray()) {
            return matchViewType(sourceType.getComponentType(), targetType.getComponentType());
        } else {
            return false;
        }
    }

    private List<DeclaredType> getConverters(Element element) {
        return Utils.getAnnotationsOn(getProcessingEnv().getElementUtils(), element, UsePropertyConverter.class, UsePropertyConverters.class)
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
        } else if (beAssigned.getKind().isPrimitive() || toAssign.getKind().isPrimitive()) {
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

    private TypeElement selectConverter(Element member, List<DeclaredType> converters, TypeMirror toType, TypeMirror fromType) {
        List<TypeElement> results = new ArrayList<>();
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
                results.add(Utils.toElement(converter));
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
                                    .map(TypeElement::getQualifiedName)
                                    .collect(Collectors.joining(", "))
            );
            return null;
        } else {
            return null;
        }
    }

    public void printBeanProviderGetInstance(@NonNull PrintWriter writer, @NonNull Type type, @NonNull BeanUsage usage, @NonNull String requester, boolean cache) {
        boolean useDefaultBeanProvider = viewOf.isUseDefaultBeanProvider();
        writer.print(getImportedName(TYPE_BEAN_PROVIDERS, SIMPLE_TYPE_BEAN_PROVIDERS));
        writer.print(".INSTANCE.get(");
        type.printType(writer, this, false, false);
        writer.print(".class, ");
        writer.print(getImportedName(TYPE_BEAN_USAGE, SIMPLE_TYPE_BEAN_USAGE));
        writer.print(".");
        writer.print(usage.name());
        writer.print(", ");
        writer.print(requester);
        writer.print(", ");
        writer.print(useDefaultBeanProvider);
        writer.print(", ");
        writer.print(cache);
        writer.print(")");
    }

    public void printInitConfigureBean(@NonNull PrintWriter writer, @NonNull String requester, boolean useConfigBeanVar) {
        if (useConfigBeanVar) {
            writer.print(READ_CONFIG_BEAN_VAR);
        } else {
            printBeanProviderGetInstance(
                    writer, configType,
                    BeanUsage.CONFIGURE,
                    requester,
                    viewOf.getConfigureBeanCacheType() == CacheType.GLOBAL
            );
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
        for (AnnotationMirror annotationMirror : viewOf.getAnnotationMirrors()) {
            Utils.printIndent(writer, INDENT, 0);
            Utils.printAnnotation(writer, annotationMirror, this, INDENT, 0);
            writer.println();
        }
        List<Property> properties = getProperties();
        boolean empty = true;
        enter(genType);
        List<Type> implTypes = new ArrayList<>();
        if (viewOf.isSerializable()) {
            implTypes.add(Type.extract(this, Serializable.class));
        }
        genType.openClass(writer, modifier, this, viewOf.getExtendsType(), implTypes, viewOf.getImplementsTypes(), INDENT, 0);
        if (viewOf.isSerializable()) {
            empty = false;
            Utils.printIndent(writer, INDENT, 1);
            writer.print("private static final long serialVersionUID = ");
            writer.print(viewOf.getSerialVersionUID());
            writer.println("L;");
            writer.println();
        }
        for (Property property : properties) {
            if (!property.isDynamic()) {
                if (empty) {
                    empty = false;
                    writer.println();
                }
                for (AnnotationMirror annotationMirror : property.collectAnnotations(this, AnnotationDest.FIELD)) {
                    Utils.printIndent(writer, INDENT, 1);
                    Utils.printAnnotation(writer, annotationMirror, this, INDENT, 1);
                    writer.println();
                }
                property.printField(writer, this, INDENT, 1);
                writer.println();
            }
        }
        if (useCachedConfigureBeanField) {
            if (empty) {
                empty = false;
                writer.println();
            }
            Utils.printIndent(writer, INDENT, 1);
            writer.print("private transient ");
            configType.printType(writer, this, true, false);
            writer.print(" ");
            writer.print(getConfigureBeanFieldVar());
            writer.println(";");
            writer.println();
        }
        modifier = viewOf.getEmptyConstructor();
        if (modifier == null) {
            modifier = Modifier.PRIVATE;
        }
        if (empty) {
            writer.println();
        }
        genType.emptyConstructor(writer, modifier, INDENT, 1);
        writer.println();
        modifier = viewOf.getFieldsConstructor();
        if (modifier != null && !properties.isEmpty()) {
            genType.fieldsConstructor(writer, this, modifier, properties, INDENT, 1);
            writer.println();
        }
        modifier = viewOf.getCopyConstructor();
        if (modifier != null) {
            genType.copyConstructor(writer, this, modifier, properties, INDENT, 1);
            writer.println();
        }
        printReadConstructor(writer);
        printReader(writer);
        printWriteBack(writer, false);
        printWriteBack(writer, true);
        for (Property property : properties) {
            if (property.hasGetter()) {
                for (AnnotationMirror annotationMirror : property.collectAnnotations(this, AnnotationDest.GETTER)) {
                    Utils.printIndent(writer, INDENT, 1);
                    Utils.printAnnotation(writer, annotationMirror, this, INDENT, 1);
                    writer.println();
                }
                property.printGetter(writer, this, INDENT, 1);
                writer.println();
            }
        }
        if (useCachedConfigureBeanField) {
            Utils.printIndent(writer, INDENT, 1);
            writer.print("public ");
            configType.printType(writer, this, true, false);
            writer.print(" ");
            writer.print(getConfigureBeanGetterVar());
            writer.println("() {");
            Utils.printIndent(writer, INDENT, 2);
            writer.print("if (");
            writer.print(getConfigureBeanFieldVar());
            writer.println(" == null) {");
            Utils.printIndent(writer, INDENT, 3);
            writer.print("this.");
            writer.print(getConfigureBeanFieldVar());
            writer.print(" = ");
            printInitConfigureBean(writer, "this", false);
            writer.println(";");
            Utils.printIndent(writer, INDENT, 2);
            writer.println("}");
            Utils.printIndent(writer, INDENT, 2);
            writer.print("return this.");
            writer.print(getConfigureBeanFieldVar());
            writer.println(";");
            Utils.printIndent(writer, INDENT, 1);
            writer.println("}");
            writer.println();
        }
        for (Property property : properties) {
            if (!property.isDynamic() && property.hasSetter()) {
                for (AnnotationMirror annotationMirror : property.collectAnnotations(this, AnnotationDest.SETTER)) {
                    Utils.printIndent(writer, INDENT, 1);
                    Utils.printAnnotation(writer, annotationMirror, this, INDENT, 1);
                    writer.println();
                }
                property.printSetter(writer, this, INDENT, 1);
                writer.println();
            }
        }

        for (ViewCodeGenerator generator : ViewCodeGenerators.INSTANCE.getGenerators()) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            generator.print(pw, this, INDENT, 1);
            pw.flush();
            String generated = sw.toString();
            if (!generated.isEmpty()) {
                writer.print(generated);
            }
        }

        printErrors(writer);
        exit();
        genType.closeClass(writer, INDENT, 0);
        return true;
    }

    private void printErrors(@NonNull PrintWriter writer) {
        if (!viewOf.isErrorMethods()) {
            return;
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

    private void printReturnNullWhenInputNull(@NonNull PrintWriter writer, String argName) {
        Utils.printIndent(writer, Context.INDENT, 2);
        writer.print("if (");
        writer.print(argName);
        writer.println(" == null) {");
        Utils.printIndent(writer, Context.INDENT, 2 + 1);
        writer.println("return null;");
        Utils.printIndent(writer, Context.INDENT, 2);
        writer.println("}");
    }

    @SuppressWarnings("SameParameterValue")
    private void printThrowNPEWhenInputNull(@NonNull PrintWriter writer, String argName, String message) {
        Utils.printIndent(writer, Context.INDENT, 2);
        writer.print("if (");
        writer.print(argName);
        writer.println(" == null) {");
        Utils.printIndent(writer, Context.INDENT, 2 + 1);
        writer.print("throw new NullPointerException(");
        if (message != null) {
            writer.print("\"");
            writer.print(StringEscapeUtils.escapeJava(message));
            writer.print("\"");
        }
        writer.println(");");
        Utils.printIndent(writer, Context.INDENT, 2);
        writer.println("}");
    }

    private String getMapKeyParameter(String parameter) {
        if (genType.getParameters().stream().anyMatch(p -> p.isTypeVar() && p.getSimpleName().equals(parameter))) {
            return getMapKeyParameter(parameter + "K");
        } else {
            return parameter;
        }
    }

    private void printCollectionReader(@NonNull PrintWriter writer, String collectionType, String collectionImpl) {
        Utils.printIndent(writer, INDENT, 1);
        Utils.printModifier(writer, Modifier.PUBLIC);
        writer.print("static ");
        String keyParameter = getMapKeyParameter("K");
        if (collectionType.equals("Map")) {
            writer.print("<");
            writer.print(keyParameter);
            if (!genType.getParameters().isEmpty()) {
                writer.print(", ");
                genType.printGenericParameters(writer, this, true, false);
            }
            writer.print("> ");
        } else {
            if (!genType.getParameters().isEmpty()) {
                genType.printGenericParameters(writer, this, true);
                writer.print(" ");
            }
        }
        if (collectionType.equals("Array")) {
            genType.printType(writer, this, true, false);
            writer.print("[] read(");
            targetType.printType(writer, this, true, false);
            writer.println("[] sources) {");
            printReturnNullWhenInputNull(writer, "sources");
            Utils.printIndent(writer, INDENT, 2);
            genType.printType(writer, this, true, false);
            writer.print("[] results = new ");
            genType.printType(writer, this, false, false);
            writer.println("[sources.length];");
            Utils.printIndent(writer, INDENT, 2);
            writer.println("for (int i = 0; i < sources.length; ++i) {");
            Utils.printIndent(writer, INDENT, 3);
            writer.println("results[i] = read(sources[i]);");
        } else {
            writer.print(collectionType);
            writer.print("<");
            if (collectionType.equals("Map")) {
                writer.print(keyParameter);
                writer.print(", ");
            }
            genType.printType(writer, this, true, false);
            writer.print("> read(");
            writer.print(collectionType);
            writer.print("<");
            if (collectionType.equals("Map")) {
                writer.print(keyParameter);
                writer.print(", ");
            }
            targetType.printType(writer, this, true, false);
            writer.println("> sources) {");
            printReturnNullWhenInputNull(writer, "sources");
            Utils.printIndent(writer, INDENT, 2);
            writer.print(collectionType);
            writer.print("<");
            if (collectionType.equals("Map")) {
                writer.print(keyParameter);
                writer.print(", ");
            }
            genType.printType(writer, this, true, false);
            writer.print("> results = new ");
            writer.print(collectionImpl);
            writer.println("<>();");
            Utils.printIndent(writer, INDENT, 2);
            writer.print("for (");
            if (collectionType.equals("Map")) {
                writer.print("Map.Entry<");
                writer.print(keyParameter);
                writer.print(", ");
                targetType.printType(writer, this, true, false);
                writer.print(">");
            } else {
                targetType.printType(writer, this, true, false);
            }
            writer.print(" source : ");
            if (collectionType.equals("Map")) {
                writer.print("sources.entrySet()");
            } else {
                writer.print("sources");
            }
            writer.println(") {");
            Utils.printIndent(writer, INDENT, 3);
            if (collectionType.equals("Map")) {
                writer.println("results.put(source.getKey(), read(source.getValue()));");
            } else {
                writer.println("results.add(read(source));");
            }
        }
        Utils.printIndent(writer, INDENT, 2);
        writer.println("}");
        Utils.printIndent(writer, INDENT, 2);
        writer.println("return results;");
        Utils.printIndent(writer, INDENT, 1);
        writer.println("}");
        writer.println();
    }

    private void prepareReadProperty(@NonNull PrintWriter writer, Property property, Map<String, String> varMap) {
        Type converter = property.getConverter();
        Property baseProperty = property.getBase();
        if (!property.isCustomMethod() && baseProperty != null && converter == null && property.isView()) {
            String var = "p" + varMap.size();
            prepareView(writer, property.getType(), var, baseProperty.getType(), property.getValueString("source"), 2, 0);
            varMap.put(property.getName(), var);
        }
    }

    private Map<String, String> prepareRead(@NonNull PrintWriter writer) {
        Map<String, String> varMap = new HashMap<>();
        for (Property property : getProperties()) {
            prepareReadProperty(writer, property, varMap);
        }
        if (useConfigureBeanVarInRead) {
            Utils.printIndent(writer, INDENT, 2);
            configType.printType(writer, this, true, false);
            writer.print(" ");
            writer.print(READ_CONFIG_BEAN_VAR);
            writer.print(" = ");
            printInitConfigureBean(writer, "source", false);
            writer.println(";");
        }
        return varMap;
    }

    private void prepareView(@NonNull PrintWriter writer, Type targetType, String targetVarName, Type sourceType, String sourceVarName, int indentNum, int level) {
        Utils.printIndent(writer, INDENT, indentNum);
        targetType.printType(writer, this, true, true);
        writer.print(" ");
        writer.print(targetVarName);
        writer.print(" = ");
        if (isViewType(targetType)) {
            targetType.printType(writer, this, true, true);
            writer.print(".read(");
            writer.print(sourceVarName);
            writer.println(");");
        } else {
            String collectionImpl;
            if (targetType.isType(List.class)) {
                collectionImpl = "ArrayList";
            } else if (targetType.isType(Set.class)) {
                collectionImpl = "HashSet";
            } else if (targetType.isType(Stack.class)) {
                collectionImpl = "Stack";
            } else if (targetType.isType(Map.class)) {
                collectionImpl = "HashMap";
            } else if (targetType.isArray()) {
                collectionImpl = null;
            } else {
                throw new IllegalArgumentException("Unsupported view collection targetType: " + targetType + ".");
            }
            writer.print("new ");
            if (targetType.isArray()) {
                targetType.withoutArray().printType(writer, this, false, false);
                writer.print("[");
                writer.print(sourceVarName);
                writer.print(".length]");
                for (int i = 0; i < targetType.getArray() - 1; ++i) {
                    writer.print("[]");
                }
                writer.println(";");
            } else {
                writer.print(collectionImpl);
                writer.println("<>();");
            }
        }
        if (!isViewType(targetType)) {
            Utils.printIndent(writer, INDENT, indentNum);
            Type targetComponentType;
            Type sourceComponentType;
            if (targetType.isType(List.class) || targetType.isType(Set.class) || targetType.isType(Stack.class)) {
                targetComponentType = targetType.getParameters().get(0);
                sourceComponentType = sourceType.getParameters().get(0);
            } else if (targetType.isType(Map.class)) {
                targetComponentType = targetType.getParameters().get(1);
                sourceComponentType = sourceType.getParameters().get(1);
            } else if (targetType.isArray()) {
                targetComponentType = Objects.requireNonNull(targetType.getComponentType());
                sourceComponentType = Objects.requireNonNull(sourceType.getComponentType());
            } else {
                throw new IllegalArgumentException("Unsupported view collection targetType: " + targetType + ".");
            }
            String elVar = "el" + level;
            writer.print("for (");
            String iVar = "i" + level;
            if (targetType.isArray()) {
                writer.print("int ");
                writer.print(iVar);
                writer.print(" = 0; ");
                writer.print(iVar);
                writer.print(" < ");
                writer.print(sourceVarName);
                writer.print(".length; ++");
                writer.print(iVar);
            } else {
                if (targetType.isType(Map.class)) {
                    writer.print("Map.Entry<");
                    sourceType.printGenericParameters(writer, this, true, false);
                    writer.print(">");
                } else {
                    sourceComponentType.printType(writer, this, true, true);
                }
                writer.print(" ");
                writer.print(elVar);
                writer.print(" : ");
                writer.print(sourceVarName);
                if (targetType.isType(Map.class)) {
                    writer.print(".entrySet()");
                }
            }
            writer.println(") {");
            if (targetType.isArray()) {
                Utils.printIndent(writer, INDENT, indentNum + 1);
                sourceComponentType.printType(writer, this, true, true);
                writer.print(" ");
                writer.print(elVar);
                writer.print(" = ");
                writer.print(sourceVarName);
                writer.print("[");
                writer.print(iVar);
                writer.println("];");
            }
            String newTargetVar = "result" + level;
            prepareView(
                    writer,
                    targetComponentType,
                    newTargetVar,
                    sourceComponentType,
                    targetType.isType(Map.class) ? elVar + ".getValue()" : elVar,
                    indentNum + 1,
                    level + 1
            );
            Utils.printIndent(writer, INDENT, indentNum + 1);
            if (targetType.isType(List.class) || targetType.isType(Set.class) || targetType.isType(Stack.class)) {
                writer.print(targetVarName);
                writer.print(".add(");
                writer.print(newTargetVar);
                writer.println(");");
            } else if (targetType.isType(Map.class)) {
                writer.print(targetVarName);
                writer.print(".put(");
                writer.print(elVar);
                writer.print(".getKey(), ");
                writer.print(newTargetVar);
                writer.println(");");
            } else if (targetType.isArray()) {
                writer.print(targetVarName);
                writer.print("[");
                writer.print(iVar);
                writer.print("] = ");
                writer.print(newTargetVar);
                writer.println(";");
            }
            Utils.printIndent(writer, INDENT, indentNum);
            writer.println("}");
        }
    }

    private void printAssignFields(@NonNull PrintWriter writer, @NonNull Map<String, String> varMap, @NonNull VarMapper varMapper, @NonNull String target) {
        for (Property property : getProperties()) {
            if (!property.isDynamic()) {
                Type converter = property.getConverter();
                Utils.printIndent(writer, INDENT, 2);
                writer.print(target);
                writer.print(".");
                writer.print(this.getMappedFieldName(property));
                writer.print(" = ");
                if (property.isCustomMethod() && property.getExtractor() != null) {
                    ((StaticMethodExtractor) property.getExtractor()).print(writer, varMapper, true, INDENT, 2);
                    writer.println(";");
                } else {
                    Property baseProperty = property.getBase();
                    if (baseProperty != null) {
                        if (converter != null) {
                            writer.print("new ");
                            converter.printType(writer, this, false, false);
                            writer.print("().convert(");
                            writer.print(baseProperty.getValueString("source"));
                            writer.println(");");
                        } else if (property.isView()) {
                            writer.print(varMap.get(property.getName()));
                            writer.println(";");
                        } else {
                            writer.print(baseProperty.getValueString("source"));
                            writer.println(";");
                        }
                    } else {
                        writer.print(varMapper.getVar(property, property.getName()));
                        writer.println(";");
                    }
                }
            }
        }
    }

    @NonNull
    private VarMapper printDefineReadArguments(@NonNull PrintWriter writer) {
        VarMapper varMapper = new VarMapper("source");
        if (extraProperties.size() + extraParams.size() > 3) {
            writer.println();
            Utils.printIndent(writer, INDENT, 2);
            targetType.printType(writer, this, true, false);
            writer.print(" source");
            for (Property extraProperty : extraProperties) {
                writer.println(",");
                Utils.printIndent(writer, INDENT, 2);
                extraProperty.printType(writer, this, true, false);
                writer.print(" ");
                writer.print(varMapper.getVar(extraProperty, extraProperty.getName()));
            }
            for (ParamInfo paramInfo : extraParams.values()) {
                writer.println(",");
                Utils.printIndent(writer, INDENT, 2);
                Type type = Type.extract(this, paramInfo.getVar());
                type.printType(writer, this, true, false);
                writer.print(" ");
                writer.print(varMapper.getVar(paramInfo, paramInfo.getExtraParamName()));
            }
            writer.println();
            Utils.printIndent(writer, INDENT, 1);
        } else {
            targetType.printType(writer, this, true, false);
            writer.print(" source");
            for (Property extraProperty : extraProperties) {
                writer.print(", ");
                extraProperty.printType(writer, this, true, false);
                writer.print(" ");
                writer.print(varMapper.getVar(extraProperty, extraProperty.getName()));
            }
            for (ParamInfo paramInfo : extraParams.values()) {
                writer.print(", ");
                Type type = Type.extract(this, paramInfo.getVar());
                type.printType(writer, this, true, false);
                writer.print(" ");
                writer.print(varMapper.getVar(paramInfo, paramInfo.getExtraParamName()));
            }
        }
        return varMapper;
    }

    private void printUseReadArguments(
            @NonNull PrintWriter writer,
            @NonNull VarMapper varMapper
    ) {
        if (extraProperties.size() + extraParams.size() > 3) {
            writer.println();
            Utils.printIndent(writer, INDENT, 2);
            writer.print("source");
            for (Property extraProperty : extraProperties) {
                writer.println(",");
                Utils.printIndent(writer, INDENT, 2);
                writer.print(varMapper.getVar(extraProperty, extraProperty.getName()));
            }
            for (ParamInfo paramInfo : extraParams.values()) {
                writer.println(",");
                Utils.printIndent(writer, INDENT, 2);
                writer.print(varMapper.getVar(paramInfo, paramInfo.getExtraParamName()));
            }
            writer.println(",");
            Utils.printIndent(writer, INDENT, 2);
        } else {
            writer.print("source");
            for (Property extraProperty : extraProperties) {
                writer.print(", ");
                writer.print(varMapper.getVar(extraProperty, extraProperty.getName()));
            }
            for (ParamInfo paramInfo : extraParams.values()) {
                writer.print(", ");
                writer.print(varMapper.getVar(paramInfo, paramInfo.getExtraParamName()));
            }
        }
    }

    private void printReader(@NonNull PrintWriter writer) {
        Utils.printIndent(writer, INDENT, 1);
        Utils.printModifier(writer, Modifier.PUBLIC);
        writer.print("static ");
        if (!genType.getParameters().isEmpty()) {
            genType.printGenericParameters(writer, this, true);
            writer.print(" ");
        }
        genType.printType(writer, this, true, false);
        writer.print(" read(");
        VarMapper varMapper = printDefineReadArguments(writer);
        writer.println(") {");
        printReturnNullWhenInputNull(writer, "source");
        if (viewOf.getReadConstructor() != null) {
            Utils.printIndent(writer, INDENT, 2);
            writer.print("return new ");
            genType.printType(writer, this, false, false);
            if (!genType.getParameters().isEmpty()) {
                writer.print("<>");
            }
            writer.print("(");
            printUseReadArguments(writer, varMapper);
            writer.println(");");
        } else {
            Map<String, String> varMap = prepareRead(writer);
            Utils.printIndent(writer, INDENT, 2);
            genType.printType(writer, this, true, false);
            writer.print(" out = new ");
            writer.print(genType.getSimpleName());
            if (!genType.getParameters().isEmpty()) {
                writer.print("<>");
            }
            writer.println("();");
            printAssignFields(writer, varMap, varMapper, "out");
            Utils.printIndent(writer, INDENT, 2);
            writer.println("return out;");
        }
        Utils.printIndent(writer, INDENT, 1);
        writer.println("}");
        writer.println();
        if (extraProperties.isEmpty() && extraParams.isEmpty()) {
            printCollectionReader(writer, "Array", "");
            printCollectionReader(writer, "List", "ArrayList");
            printCollectionReader(writer, "Set", "HashSet");
            printCollectionReader(writer, "Stack", "Stack");
            printCollectionReader(writer, "Map", "HashMap");
        }
    }

    private void printReadConstructor(@NonNull PrintWriter writer) {
        if (viewOf.getReadConstructor() == null) {
            return;
        }
        Utils.printIndent(writer, INDENT, 1);
        Utils.printModifier(writer, viewOf.getReadConstructor());
        genType.printType(writer, this, false, false);
        writer.print("(");
        VarMapper varMapper = printDefineReadArguments(writer);
        writer.println(") {");
        String npeMessage = "The input source argument of the read constructor of class " + genType.getQualifiedName() + " should not be null.";
        printThrowNPEWhenInputNull(writer, "source", npeMessage);
        Map<String, String> varMap = prepareRead(writer);
        printAssignFields(writer, varMap, varMapper, "this");
        Utils.printIndent(writer, INDENT, 1);
        writer.println("}");
        writer.println();
    }

    private void printWriteBackField(@NonNull PrintWriter writer, @NonNull Property property) {
        if (property.getConverter() != null) {
            writer.print("new ");
            property.getConverter().printType(writer, this, false, false);
            writer.print("().convertBack(");
            writer.print("this.");
            writer.print(getMappedFieldName(property));
            writer.println(");");
        } else {
            writer.print("this.");
            writer.print(getMappedFieldName(property));
        }
    }

    private void printWriteBack(@NonNull PrintWriter writer, boolean create) {
        if (!create && viewOf.getWriteBackMethod() == Access.NONE) {
            return;
        }
        if (create && viewOf.getCreateAndWriteBackMethod() == Access.NONE) {
            return;
        }
        Utils.printIndent(writer, INDENT, 1);
        Utils.printAccess(writer, create ? viewOf.getCreateAndWriteBackMethod() : viewOf.getWriteBackMethod());
        if (create) {
            getTargetType().printType(writer, this, true, false);
            writer.print(" createAndWriteBack(");
        } else {
            writer.print("void writeBack(");
        }
        if (!create) {
            getTargetType().printType(writer, this, true, false);
            writer.print(" target");
        }
        writer.print(") {");
        if (create) {
            writer.println();
            Utils.printIndent(writer, INDENT, 2);
            getTargetType().printType(writer, this, true, false);
            writer.print(" target = ");
            printBeanProviderGetInstance(writer, targetType, BeanUsage.CONVERT_BACK, "this", false);
            writer.print(";");
        }
        for (Property property : getProperties()) {
            if (property.isWriteable() || property.isLombokWritable(samePackage)) {
                if (!viewOf.getWriteExcludes().contains(property.getName())) {
                    writer.println();
                    Utils.printIndent(writer, INDENT, 2);
                    if (property.isWriteMethod()) {
                        writer.print("target.");
                        writer.print(property.getSetterName());
                        writer.print("(");
                        printWriteBackField(writer, property);
                        writer.print(");");
                    } else {
                        writer.print("target.");
                        writer.print(Objects.requireNonNull(property.getField()).getName());
                        writer.print(" = ");
                        printWriteBackField(writer, property);
                        writer.print(";");
                    }
                }
            }
        }
        if (create) {
            writer.println();
            Utils.printIndent(writer, INDENT, 2);
            writer.print("return target;");
        }
        writer.println();
        Utils.printIndent(writer, INDENT, 1);
        writer.println("}");
        writer.println();
    }
}
