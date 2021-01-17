package io.github.vipcxj.beanknife.jpa;

import io.github.vipcxj.beanknife.core.models.Property;
import io.github.vipcxj.beanknife.core.models.StaticMethodExtractor;
import io.github.vipcxj.beanknife.core.models.Type;
import io.github.vipcxj.beanknife.core.models.ViewContext;
import io.github.vipcxj.beanknife.core.utils.ParamInfo;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.jpa.runtime.annotations.AddJpaSupport;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;

public class JpaContext {
    public static final String TYPE_CRITERIA_BUILDER = "javax.persistence.criteria.CriteriaBuilder";
    public static final String SIMPLE_TYPE_CRITERIA_BUILDER = "CriteriaBuilder";
    public static final String TYPE_SELECTION = "javax.persistence.criteria.Selection";
    public static final String SIMPLE_TYPE_SELECTION = "Selection";
    public static final String TYPE_ROOT = "javax.persistence.criteria.Root";
    public static final String SIMPLE_TYPE_ROOT = "Root";
    private final ViewContext viewContext;
    private boolean enabled;
    private boolean fixConstructor;
    private boolean useSource;
    private long argsNum;
    private final List<Property> properties;
    private final List<Property> viewProperties;
    private final List<Property> extraProperties;
    private final List<ParamInfo> paramInfos;

    public JpaContext(ViewContext viewContext) {
        this.viewContext = viewContext;
        this.properties = new ArrayList<>();
        this.viewProperties = new ArrayList<>();
        this.extraProperties = new ArrayList<>();
        this.paramInfos = new ArrayList<>();
        checkEnabled();
    }

    private void checkEnabled() {
        Types typeUtils = viewContext.getProcessingEnv().getTypeUtils();
        TypeElement targetElement = viewContext.getViewOf().getTargetElement();
        TypeElement configElement = viewContext.getViewOf().getConfigElement();
        Elements elementUtils = viewContext.getProcessingEnv().getElementUtils();
        List<AnnotationMirror> annotations = Utils.getAnnotationsOn(elementUtils, configElement, AddJpaSupport.class, null);
        if (!annotations.isEmpty()) {
            AnnotationMirror addJpaSupport = annotations.get(annotations.size() - 1);
            List<TypeMirror> targets = Utils.getTypeArrayAnnotationValue(addJpaSupport, "value");
            if (targets == null) {
                if (Utils.getAnnotationDirectOn(configElement, "javax.persistence.Entity") != null) {
                    enabled = true;
                    return;
                }
            } else {
                if (targets.stream().anyMatch(target -> typeUtils.isSameType(target, targetElement.asType()))) {
                    enabled = true;
                    return;
                }
            }
            List<TypeMirror> extraTargets = Utils.getTypeArrayAnnotationValue(addJpaSupport, "extraTargets");
            if (extraTargets != null && extraTargets.stream().anyMatch(target -> typeUtils.isSameType(target, targetElement.asType()))) {
                enabled = true;
            }
        }
        if (enabled) {
            viewContext.importVariable(TYPE_SELECTION, SIMPLE_TYPE_SELECTION);
            viewContext.importVariable(TYPE_CRITERIA_BUILDER, SIMPLE_TYPE_CRITERIA_BUILDER);
            viewContext.importVariable(TYPE_ROOT, SIMPLE_TYPE_ROOT);
            for (Property property : viewContext.getProperties()) {
                if (!property.isDynamic()) {
                    if (property.isCustomMethod() && property.getExtractor() != null) {
                        List<ParamInfo> paramInfoList = ((StaticMethodExtractor) property.getExtractor()).getParamInfoList();
                        for (ParamInfo paramInfo : paramInfoList) {
                            if (paramInfo.isSource()) {
                                useSource = true;
                            } else if (paramInfo.isPropertyParam()) {
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
                                    viewProperties.add(property);
                                }
                                addProperty(base);
                            }
                        } else {
                            if (supportType(property.getType())) {
                                extraProperties.add(property);
                            }
                        }
                    }
                }
            }
            checkConstructor();
        }
    }

    private boolean supportType(Type type) {
        return !type.isCollection() && !type.isMap() && !type.isArray();
    }

    private void addProperty(Property property) {
        if (properties.stream().noneMatch(p -> p.getName().equals(property.getName()))) {
            properties.add(property);
        }
    }

    private void addParamInfo(ParamInfo paramInfo) {
        String extraParamName = paramInfo.getExtraParamName();
        if (paramInfos.stream().noneMatch(info -> info.getExtraParamName().equals(extraParamName))) {
            paramInfos.add(viewContext.getExtraParams().get(extraParamName));
        }
    }

    private void checkConstructor() {
        long fieldsCount = viewContext.getProperties().size() - viewContext.getProperties().stream().filter(Property::isDynamic).count();
        argsNum = properties.size() + extraProperties.size() + paramInfos.size() + (useSource ? 1 : 0);
        List<Type> types = new ArrayList<>();
        if (useSource) {
            types.add(viewContext.getTargetType());
        }
        for (Property property : properties) {
            types.add(property.getType());
        }
        for (Property extraProperty : extraProperties) {
            types.add(extraProperty.getType());
        }
        for (ParamInfo paramInfo : paramInfos) {
            types.add(Type.extract(viewContext, paramInfo.getVar()));
        }
        Types typeUtils = viewContext.getProcessingEnv().getTypeUtils();
        fixConstructor = true;
        if (argsNum == fieldsCount) {
            int i = 0;
            for (Property property : viewContext.getProperties()) {
                if (!property.isDynamic()) {
                    Type type = types.get(i++);
                    if (!typeUtils.isSameType(typeUtils.erasure(type.getTypeMirror()), typeUtils.erasure(property.getType().getTypeMirror()))) {
                        fixConstructor = false;
                        break;
                    }
                }
            }
        }
        if (fixConstructor) {
            argsNum += 1;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public List<Property> getProperties() {
        return properties;
    }

    public List<Property> getViewProperties() {
        return viewProperties;
    }

    public List<Property> getExtraProperties() {
        return extraProperties;
    }

    public List<ParamInfo> getParamInfos() {
        return paramInfos;
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
