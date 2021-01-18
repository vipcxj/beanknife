package io.github.vipcxj.beanknife.jpa;

import io.github.vipcxj.beanknife.core.models.Property;
import io.github.vipcxj.beanknife.core.models.StaticMethodExtractor;
import io.github.vipcxj.beanknife.core.models.Type;
import io.github.vipcxj.beanknife.core.models.ViewContext;
import io.github.vipcxj.beanknife.core.utils.ParamInfo;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.core.utils.VarMapper;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;

public class JpaContext {
    public static final String TYPE_ENTITY = "javax.persistence.Entity";
    public static final String TYPE_EMBEDDABLE = "javax.persistence.Embeddable";
    public static final String TYPE_CRITERIA_BUILDER = "javax.persistence.criteria.CriteriaBuilder";
    public static final String SIMPLE_TYPE_CRITERIA_BUILDER = "CriteriaBuilder";
    public static final String TYPE_SELECTION = "javax.persistence.criteria.Selection";
    public static final String SIMPLE_TYPE_SELECTION = "Selection";
    public static final String TYPE_FROM = "javax.persistence.criteria.From";
    public static final String SIMPLE_TYPE_FROM = "From";
    private static final String TYPE_ADD_JPA_SUPPORT = "io.github.vipcxj.beanknife.jpa.runtime.annotations.AddJpaSupport";
    private final ViewContext viewContext;
    private boolean enabled;
    private boolean fixConstructor;
    private boolean useSource;
    private long argsNum;
    private String preventConflictArgVar = "preventConflictArg";
    private final VarMapper constructorVarMapper;
    private final Map<Property, String> propertiesMap;
    private final List<Property> properties;
    private final List<ParamInfo> paramInfos;

    public JpaContext(ViewContext viewContext) {
        this.viewContext = viewContext;
        this.constructorVarMapper = new VarMapper();
        this.propertiesMap = new IdentityHashMap<>();
        this.properties = new ArrayList<>();
        this.paramInfos = new ArrayList<>();
        checkEnabled();
    }

    private void checkEnabled() {
        Types typeUtils = viewContext.getProcessingEnv().getTypeUtils();
        TypeElement targetElement = viewContext.getViewOf().getTargetElement();
        TypeElement configElement = viewContext.getViewOf().getConfigElement();
        Elements elementUtils = viewContext.getProcessingEnv().getElementUtils();
        System.out.println("Scanning " + configElement.getQualifiedName() + "...");
        List<AnnotationMirror> annotations = Utils.getAnnotationsOn(elementUtils, configElement, TYPE_ADD_JPA_SUPPORT, null, true, true);
        if (!annotations.isEmpty()) {
            System.out.println("Found AddJpaSupport");
            AnnotationMirror addJpaSupport = annotations.get(annotations.size() - 1);
            List<TypeMirror> targets = Utils.getTypeArrayAnnotationValue(addJpaSupport, "value");
            if (targets == null) {
                if (Utils.getAnnotationDirectOn(targetElement, TYPE_ENTITY) != null) {
                    enabled = true;
                }
            } else {
                if (targets.stream().anyMatch(target -> typeUtils.isSameType(target, targetElement.asType()))) {
                    enabled = true;
                }
            }
            if (!enabled) {
                List<TypeMirror> extraTargets = Utils.getTypeArrayAnnotationValue(addJpaSupport, "extraTargets");
                if (extraTargets != null && extraTargets.stream().anyMatch(target -> typeUtils.isSameType(target, targetElement.asType()))) {
                    enabled = true;
                }
            }
        }
        System.out.println("Enabled: " + enabled);
        if (enabled) {
            viewContext.importVariable(TYPE_SELECTION, SIMPLE_TYPE_SELECTION);
            viewContext.importVariable(TYPE_CRITERIA_BUILDER, SIMPLE_TYPE_CRITERIA_BUILDER);
            viewContext.importVariable(TYPE_FROM, SIMPLE_TYPE_FROM);
            if (viewContext.getProperties().stream().anyMatch(property -> {
                if (!property.isDynamic() && property.isCustomMethod() && property.getExtractor() != null) {
                    List<ParamInfo> paramInfoList = ((StaticMethodExtractor) property.getExtractor()).getParamInfoList();
                    return paramInfoList.stream().anyMatch(ParamInfo::isSource);
                }
                return false;
            })) {
                useSource = true;
                constructorVarMapper.addInitVar("source");
            }
            for (Property property : viewContext.getProperties()) {
                if (!property.isDynamic()) {
                    if (property.isCustomMethod() && property.getExtractor() != null) {
                        List<ParamInfo> paramInfoList = ((StaticMethodExtractor) property.getExtractor()).getParamInfoList();
                        for (ParamInfo paramInfo : paramInfoList) {
                            if (!useSource && paramInfo.isPropertyParam()) {
                                Property injectedProperty = paramInfo.getInjectedProperty();
                                Type type = injectedProperty.getType();
                                if (supportType(type)) {
                                    addProperty(injectedProperty);
                                }
                            } else if (paramInfo.isExtraParam()) {
                                Type type = Type.extract(viewContext, paramInfo.getVar());
                                if (supportType(type)) {
                                    addParamInfo(paramInfo);
                                }
                            }
                        }
                    } else {
                        Property base = property.getBase();
                        if (base != null) {
                            if (supportType(base.getType())) {
                                if (property.getConverter() == null && property.isView()) {
                                    addProperty(property);
                                } else {
                                    addProperty(base);
                                }
                            }
                        } else {
                            if (supportType(property.getType())) {
                                addProperty(property);
                            }
                        }
                    }
                }
            }
            // 最终properties里就三种Property, baseProperty，viewProperty，extraProperty
            checkConstructor();
        }
    }

    public static boolean supportType(Type type) {
        return !type.getQualifiedName().equals("java.util.List")
                && !type.getQualifiedName().equals("java.util.Set")
                && !type.getQualifiedName().equals("java.util.Collection")
                && !type.getQualifiedName().equals("java.util.Map")
                && !type.isArray();
    }

    private boolean isObjectDirectWithAnnotation(Type type, String annotationName) {
        if (!type.isObject()) {
            return false;
        }
        Elements elementUtils = viewContext.getProcessingEnv().getElementUtils();
        TypeElement typeElement = elementUtils.getTypeElement(type.getQualifiedName());
        AnnotationMirror entityAnnotation = Utils.getAnnotationDirectOn(typeElement, annotationName);
        return entityAnnotation != null;
    }

    public boolean isEntity(Type type) {
        return isObjectDirectWithAnnotation(type, TYPE_ENTITY);
    }

    public boolean isEmbeddable(Type type) {
        return isObjectDirectWithAnnotation(type, TYPE_EMBEDDABLE);
    }

    public void addProperty( Property property) {
        String old = propertiesMap.put(property, property.getName());
        if (old == null) {
            properties.add(property);
        }
        constructorVarMapper.getVar(property, property.getName());
    }

    private void addParamInfo(ParamInfo paramInfo) {
        String extraParamName = paramInfo.getExtraParamName();
        if (paramInfos.stream().noneMatch(info -> info.getExtraParamName().equals(extraParamName))) {
            constructorVarMapper.getVar(paramInfo, extraParamName);
            paramInfos.add(viewContext.getExtraParams().get(extraParamName));
        }
    }

    private void checkConstructor() {
        long fieldsCount = viewContext.getProperties().size() - viewContext.getProperties().stream().filter(Property::isDynamic).count();
        argsNum = propertiesMap.size() + paramInfos.size() + (useSource ? 1 : 0);
        List<Type> types = new ArrayList<>();
        if (useSource) {
            types.add(viewContext.getTargetType());
        }
        for (Property property : properties) {
            types.add(property.getType());
        }
        for (ParamInfo paramInfo : paramInfos) {
            types.add(Type.extract(viewContext, paramInfo.getVar()));
        }
        fixConstructor = true;
        if (argsNum == fieldsCount) {
            int i = 0;
            for (Property property : viewContext.getProperties()) {
                if (!property.isDynamic()) {
                    Type type = types.get(i++);
                    if (!type.sameType(property.getType(), true)) {
                        fixConstructor = false;
                        break;
                    }
                }
            }
        }
        if (fixConstructor) {
            argsNum += 1;
            this.preventConflictArgVar = constructorVarMapper.appendVar(preventConflictArgVar);
        }
    }

    public VarMapper getConstructorVarMapper() {
        return constructorVarMapper;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<ParamInfo> getParamInfos() {
        return paramInfos;
    }

    public String getPreventConflictArgVar() {
        return preventConflictArgVar;
    }

    public boolean isUseSource() {
        return useSource;
    }

    public boolean isFixConstructor() {
        return fixConstructor;
    }

    public long getArgsNum() {
        return argsNum;
    }
}
