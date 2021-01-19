package io.github.vipcxj.beanknife.jpa;

import io.github.vipcxj.beanknife.core.models.Property;
import io.github.vipcxj.beanknife.core.models.StaticMethodExtractor;
import io.github.vipcxj.beanknife.core.models.Type;
import io.github.vipcxj.beanknife.core.models.ViewContext;
import io.github.vipcxj.beanknife.core.utils.ParamInfo;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.core.utils.VarMapper;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PropertyData {
    private final JpaContext jpaContext;
    private final ViewContext context;
    private final Property target;
    private final PropertyData parent;
    private PropertyType propertyType;
    private boolean useSource;
    private boolean provideSource;
    private ViewContext subContext;
    private List<ParamInfo> extractorPropertyData;
    private List<PropertyData> viewPropertyData;

    public PropertyData(JpaContext jpaContext, ViewContext context, Property target, PropertyData parent) {
        this.jpaContext = jpaContext;
        this.context = context;
        this.target = target;
        this.parent = parent;
    }

    public static List<PropertyData> collectData(JpaContext jpaContext) {
        return collectData(jpaContext, jpaContext.getViewContext(), null, null);
    }

    public static List<PropertyData> collectData(JpaContext jpaContext, ViewContext context, Set<String> typePool, PropertyData parent) {
        if (typePool == null) {
            typePool = new HashSet<>();
        }
        List<PropertyData> dataList = new ArrayList<>();
        for (Property property : context.getProperties()) {
            PropertyData propertyData = create(jpaContext, context, property, typePool, parent);
            if (propertyData != null) {
                dataList.add(propertyData);
            }
        }
        return dataList;
    }

    public static PropertyData create(JpaContext jpaContext, ViewContext context, Property property, Set<String> typePool, PropertyData parent) {
        PropertyData propertyData = new PropertyData(jpaContext, context, property, parent);
        String typeName = property.getType().getQualifiedName();
        if (typePool.contains(typeName)) {
            propertyData.propertyType = PropertyType.UNAVAIABLE;
            return propertyData;
        } else {
            typePool.add(typeName);
        }
        if (property.isDynamic()) {
            return null;
        }
        if (propertyData.target.isCustomMethod()) {
            propertyData.propertyType = PropertyType.EXTRACTOR;
            StaticMethodExtractor extractor = (StaticMethodExtractor) propertyData.target.getExtractor();
            assert extractor != null;
            propertyData.extractorPropertyData = extractor.getParamInfoList();
            propertyData.useSource = propertyData.extractorPropertyData.stream().anyMatch(ParamInfo::isSource);
            if (!propertyData.useSource && propertyData.extractorPropertyData.stream().anyMatch(paramInfo -> {
                if (paramInfo.isExtraParam()) {
                    Type type = Type.extract(context, paramInfo.getVar());
                    if (!JpaContext.supportType(type)) {
                        return true;
                    }
                }
                return paramInfo.isPropertyParam() && !JpaContext.supportType(paramInfo.getInjectedProperty().getType());
            })) {
                propertyData.propertyType = PropertyType.UNAVAIABLE;
                return propertyData;
            }
        } else {
            Property base = propertyData.target.getBase();
            if (base != null) {
                if (!JpaContext.supportType(base.getType())) {
                    propertyData.propertyType = PropertyType.UNAVAIABLE;
                    return propertyData;
                }
                if (property.isView()) {
                    propertyData.propertyType = PropertyType.VIEW;
                    ViewContext subContext = context.getViewContext(property.getType());
                    if (subContext == null) {
                        throw new IllegalStateException("This is impossible.");
                    }
                    propertyData.subContext = subContext;
                    propertyData.viewPropertyData = collectData(jpaContext, subContext, typePool, propertyData);
                    propertyData.provideSource = propertyData.viewPropertyData.stream().anyMatch(PropertyData::isUseSource);
                } else {
                    propertyData.propertyType = PropertyType.BASEABLE;
                }
            } else {
                propertyData.propertyType = PropertyType.EXTRA;
            }
        }
        return propertyData;
    }

    public boolean isUseSource() {
        return useSource;
    }

    public boolean isProvideSource() {
        return provideSource;
    }

    private ParamInfo normParamInfo(ParamInfo paramInfo) {
        return context.getExtraParams().get(paramInfo.getExtraParamName());
    }

    private static String capitalizeHeader(String value) {
        if (value == null) {
            return null;
        }
        if (value.isEmpty()) {
            return value;
        }
        if (value.length() == 1) {
            return value.toUpperCase();
        }
        return Character.toUpperCase(value.codePointAt(0)) + value.substring(1);
    }

    private String fixName(String name) {
        if (parent != null) {
            return parent.getTarget().getName() + capitalizeHeader(name);
        } else {
            return name;
        }
    }

    public void ready() {
        boolean parentProvideSource = parentProvideSource();
        VarMapper constructorVarMapper = jpaContext.getConstructorVarMapper();
        VarMapper methodVarMapper = jpaContext.getSelectionMethodVarMapper();
        switch (propertyType) {
            case BASEABLE:
                if (!parentProvideSource) {
                    assert target.getBase() != null;
                    constructorVarMapper.getVar(target.getBase(), fixName(target.getBase().getName()));
                }
            case EXTRA: {
                String name = fixName(target.getName());
                constructorVarMapper.getVar(target, name);
                methodVarMapper.getVar(target, name);
            }
            case EXTRACTOR: {
                for (ParamInfo paramInfo : extractorPropertyData) {
                    if (!parentProvideSource && paramInfo.isPropertyParam()) {
                        Property injectedProperty = paramInfo.getInjectedProperty();
                        constructorVarMapper.getVar(injectedProperty, fixName(injectedProperty.getName()));
                    }
                    if (paramInfo.isExtraParam()) {
                        paramInfo = normParamInfo(paramInfo);
                        String name = fixName(paramInfo.getExtraParamName());
                        constructorVarMapper.getVar(paramInfo, name);
                        methodVarMapper.getVar(paramInfo, name);
                    }
                }
            }
            case VIEW:
                if (!parentProvideSource && this.provideSource) {
                    constructorVarMapper.getVar(target, fixName(target.getName()));
                }
                for (PropertyData propertyData : viewPropertyData) {
                    propertyData.ready();
                }
        }
    }

    private boolean parentProvideSource() {
        return parent != null ? parent.isProvideSource() : jpaContext.isProvideSource();
    }

    private String getSourceInConstructor() {
        Property base = target.getBase();
        assert base != null;
        if (parentProvideSource()) {
            String parentSource = parent != null ? parent.getSourceInConstructor() : JpaContext.ARG_SOURCE;
            if (base.isMethod()) {
                return parentSource + "." + target.getGetterName() + "()";
            }
        }
    }

    public void prepareConstructor(PrintWriter writer, boolean useSource, String indent, int indentNum) {
        if (propertyType == PropertyType.VIEW) {
            if (useSource) {

            } else {

            }
            for (PropertyData propertyData : viewPropertyData) {
                propertyData.prepareConstructor(writer, indent, indentNum);
            }

        }
    }

    public void printAssignmentInConstructor(PrintWriter writer, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        switch (propertyType) {
            case BASEABLE:

        }
    }

    public Property getTarget() {
        return target;
    }
}
