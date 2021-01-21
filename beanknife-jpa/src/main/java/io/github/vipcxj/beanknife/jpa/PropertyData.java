package io.github.vipcxj.beanknife.jpa;

import io.github.vipcxj.beanknife.core.models.Property;
import io.github.vipcxj.beanknife.core.models.StaticMethodExtractor;
import io.github.vipcxj.beanknife.core.models.Type;
import io.github.vipcxj.beanknife.core.models.ViewContext;
import io.github.vipcxj.beanknife.core.utils.ParamInfo;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.core.utils.VarMapper;
import io.github.vipcxj.beanknife.runtime.utils.BeanUsage;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import java.io.PrintWriter;
import java.util.*;

public class PropertyData {
    private final JpaContext jpaContext;
    private final ViewContext context;
    private final Property target;
    private final PropertyData parent;
    private PropertyType propertyType;
    private boolean ignore;
    private boolean needParentSource;
    private boolean provideAsSource;
    private boolean useReaderMethod;
    private boolean useFieldsConstructor;
    private boolean constructNeedReflect;
    private ViewContext subContext;
    private List<PropertyData> viewPropertyData;
    private StaticMethodExtractor extractor;

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

    private void cleanBasePropertyArg() {
        if (propertyType == PropertyType.VIEW) {
            for (Property baseProperty : subContext.getBaseProperties()) {
                jpaContext.getConstructorVarMapper().removeVar(baseProperty);
            }
        }
    }

    private void markParentProvideSource() {
        if (parent != null) {
            parent.provideAsSource = true;
            parent.cleanBasePropertyArg();
        } else {
            jpaContext.markProvideSource();
        }
    }

    public static PropertyData create(JpaContext jpaContext, ViewContext context, Property property, Set<String> typePool, PropertyData parent) {
        if (property.isDynamic()) {
            return null;
        }
        PropertyData propertyData = new PropertyData(jpaContext, context, property, parent);
        if (property.getType().isView()) {
            String typeName = property.getType().getQualifiedName();
            if (typePool.contains(typeName)) {
                propertyData.ignore = true;
            } else {
                typePool.add(typeName);
            }
        }
        if (propertyData.target.isCustomMethod()) {
            propertyData.propertyType = PropertyType.EXTRACTOR;
            propertyData.extractor = (StaticMethodExtractor) propertyData.target.getExtractor();
            assert propertyData.extractor != null;
            List<ParamInfo> paramInfoList = propertyData.extractor.getParamInfoList();
            propertyData.needParentSource = paramInfoList
                    .stream()
                    .anyMatch(paramInfo -> paramInfo.isSource() || (paramInfo.isPropertyParam() && JpaContext.unSupportType(paramInfo.getInjectedProperty().getType())));
            if (propertyData.needParentSource) {
                propertyData.markParentProvideSource();
            }
            if (propertyData.needParentSource && paramInfoList
                    .stream()
                    .anyMatch(paramInfo -> paramInfo.isPropertyParam() && !paramInfo.getInjectedProperty().sourceCanSeeFrom(jpaContext.getViewContext().getPackageName()))
            ) {
                jpaContext.importReflect();
            }
            for (ParamInfo paramInfo : paramInfoList) {
                if (!propertyData.parentProvideSource() && paramInfo.isPropertyParam()) {
                    Property injectedProperty = paramInfo.getInjectedProperty();
                    String name = injectedProperty.getName();
                    jpaContext.newConstructorVar(injectedProperty, propertyData.fixName(name), ArgData.pathVar(propertyData.getPath(name)));
                }
                if (paramInfo.isExtraParam()) {
                    paramInfo = propertyData.normParamInfo(paramInfo);
                    String name = propertyData.fixName(paramInfo.getExtraParamName());
                    jpaContext.newConstructorVar(paramInfo, name, ArgData.extraVar(paramInfo));
                    jpaContext.getSelectionMethodVarMapper().getVar(paramInfo, name);
                }
            }
        } else {
            Property base = propertyData.target.getBase();
            if (base != null) {
                boolean unSupportType = JpaContext.unSupportType(base.getType());
                if (property.isView()) {
                    propertyData.propertyType = PropertyType.VIEW;
                    propertyData.subContext = context.getViewContext(property.getType());
                    assert propertyData.subContext != null;
                    if (unSupportType) {
                        if (!propertyData.subContext.hasExtraParams() && !propertyData.subContext.hasExtraProperties()) {
                            propertyData.markParentProvideSource();
                            propertyData.useReaderMethod = true;
                        } else {
                            propertyData.ignore = true;
                        }
                    }
                    if (!propertyData.ignore && !propertyData.useReaderMethod) {
                        propertyData.viewPropertyData = collectData(jpaContext, propertyData.subContext, typePool, propertyData);
                        propertyData.useReaderMethod = (propertyData.parentProvideSource() || propertyData.provideAsSource) && propertyData.viewPropertyData
                                .stream()
                                .noneMatch(data -> data.ignore && data.propertyType != PropertyType.BASEABLE && data.propertyType != PropertyType.EXTRA);
                        if (!propertyData.parentProvideSource() && propertyData.provideAsSource) {
                            jpaContext.newConstructorVar(base, propertyData.fixName(base.getName()), ArgData.pathVar(propertyData.getPath(base.getName())));
                        }
                        if (!propertyData.useReaderMethod) {
                            if (propertyData.subContext.hasFieldsConstructor()) {
                                propertyData.useFieldsConstructor = true;
                                propertyData.constructNeedReflect = !propertyData.subContext.canSeeFieldsConstructor(jpaContext.getViewContext().getPackageName());
                            } else {
                                propertyData.constructNeedReflect = !propertyData.subContext.canSeeEmptyConstructor(jpaContext.getViewContext().getPackageName());
                            }
                            if (propertyData.constructNeedReflect) {
                                jpaContext.importReflect();
                            }
                        }
                    }
                } else {
                    propertyData.propertyType = PropertyType.BASEABLE;
                    if (unSupportType) {
                        propertyData.needParentSource = true;
                        propertyData.markParentProvideSource();
                    }
                    if (!propertyData.parentProvideSource()) {
                        jpaContext.newConstructorVar(base, propertyData.fixName(base.getName()), ArgData.pathVar(propertyData.getPath(base.getName())));
                    }
                }
                if (propertyData.parentProvideSource() && !canAccessFromProperty(jpaContext, base)) {
                    jpaContext.importReflect();
                }
            } else {
                propertyData.propertyType = PropertyType.EXTRA;
                if (JpaContext.unSupportType(property.getType())) {
                    propertyData.ignore = true;
                }
                if (!propertyData.ignore) {
                    String name = propertyData.fixName(property.getName());
                    jpaContext.newConstructorVar(property, name, ArgData.extraVar(property));
                    jpaContext.getSelectionMethodVarMapper().getVar(property, name);
                }
            }
        }
        return propertyData;
    }

    public PropertyType getPropertyType() {
        return propertyType;
    }

    public boolean isNeedParentSource() {
        return needParentSource;
    }

    public boolean isProvideAsSource() {
        return provideAsSource;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void markConstructNeedReflect() {
        this.constructNeedReflect = true;
        this.jpaContext.importReflect();
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
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

    public List<String> getPath() {
        return getPath(null);
    }

    public List<String> getPath(String name) {
        if (name == null) {
            name = target.getBase() != null ? target.getBase().getName() : target.getName();
        }
        if (parent == null) {
            return Collections.singletonList(name);
        } else {
            List<String> path = new ArrayList<>(parent.getPath());
            path.add(name);
            return path;
        }
    }

    private String fixName(String name) {
        if (parent != null) {
            List<String> path = parent.getPath();
            Optional<String> parentName = path.stream().reduce((n1, n2) -> n1 + capitalizeHeader(n2));
            return parentName.map(s -> (s + capitalizeHeader(name))).orElse(name);
        } else {
            return name;
        }
    }

    private boolean parentProvideSource() {
        return parent != null ? parent.isProvideAsSource() : jpaContext.isProvideSource();
    }

    private static String getPropertyPackageName(JpaContext jpaContext, Property property) {
        Elements elementUtils = jpaContext.getViewContext().getProcessingEnv().getElementUtils();
        PackageElement propertyPackage = elementUtils.getPackageOf(property.getElement());
        return propertyPackage.getQualifiedName().toString();
    }

    private static boolean canAccessFromProperty(JpaContext jpaContext, Property property) {
        String propertyPackage = getPropertyPackageName(jpaContext, property);
        String packageName = jpaContext.getViewContext().getPackageName();
        boolean samePackage = packageName.equals(propertyPackage);
        Modifier modifier = property.getModifier();
        if (samePackage) {
            return modifier != null && modifier != Modifier.PRIVATE;
        } else {
            return modifier == Modifier.PUBLIC;
        }
    }

    private String getReflectUtilsType() {
        return jpaContext.getViewContext().getImportedName(JpaContext.TYPE_REFLECT_UTILS, JpaContext.SIMPLE_TYPE_REFLECT_UTILS);
    }

    private String getSourceVarInConstructor() {
        if (ignore) {
            return null;
        }
        if (propertyType == PropertyType.BASEABLE) {
            Property base = target.getBase();
            assert base != null;
            if (parentProvideSource()) {
                return getPropertyVar(base, getParentSourceVar());
            } else {
                return jpaContext.getConstructorVarMapper().getVar(base);
            }
        } else if (propertyType == PropertyType.EXTRA) {
            return jpaContext.getConstructorVarMapper().getVar(target);
        } else if (propertyType == PropertyType.EXTRACTOR) {
            return null;
        } else if (propertyType == PropertyType.VIEW) {
            Property base = target.getBase();
            assert base != null;
            if (parentProvideSource()) {
                return getPropertyVar(base, getParentSourceVar());
            } else if (provideAsSource) {
                return jpaContext.getConstructorVarMapper().getVar(base);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private boolean appendMethodArg(PrintWriter writer, String var, boolean start, boolean breakLine, String indent, int indentNum) {
        return Utils.appendMethodArg(writer, w -> w.print(var), start, breakLine, indent, indentNum);
    }

    private String getParentSourceVar() {
        return parent != null ? parent.getSourceVarInConstructor() : jpaContext.getConstructorVarMapper().getVar(JpaContext.SOURCE_ARG_KEY);
    }

    private String getConfigBeanVar() {
        return jpaContext.getConstructorVarMapper().getVar(context.getConfigType().getQualifiedName());
    }

    private String getPropertyVar(Property property, String parentSourceVar) {
        if (property.sourceCanSeeFrom(jpaContext.getViewContext().getPackageName())) {
            if (property.isMethod()) {
                return parentSourceVar + "." + property.getGetterName() + "()";
            } else {
                return parentSourceVar + "." + property.getName();
            }
        } else {
            return getReflectUtilsType() +
                    ".getProperty(" + parentSourceVar + ", " +
                    property.getName() + ", " +
                    (property.isMethod() ? property.getGetterName() : "null") + ")";
        }
    }

    private void printExtractor(PrintWriter writer, String indent, int indentNum) {
        assert propertyType == PropertyType.EXTRACTOR;
        if (extractor.useBeanProvider()) {
            writer.print(getConfigBeanVar());
        } else {
            context.getConfigType().printType(writer, jpaContext.getViewContext(), false, false);
        }
        writer.print(".");
        writer.print(extractor.getExecutableElement().getSimpleName().toString());
        writer.print("(");
        boolean start = true;
        boolean breakLine = extractor.getParamInfoList().size() > 3;
        for (ParamInfo paramInfo : extractor.getParamInfoList()) {
            String var = null;
            if (paramInfo.isSource()) {
                var = getParentSourceVar();
            } else if (paramInfo.isPropertyParam()) {
                Property injectedProperty = paramInfo.getInjectedProperty();
                if (parentProvideSource()) {
                    String parentSourceVar = getParentSourceVar();
                    var = getPropertyVar(injectedProperty, parentSourceVar);
                } else {
                    var = jpaContext.getConstructorVarMapper().getVar(injectedProperty);
                }
            } else if (paramInfo.isExtraParam()) {
                var = jpaContext.getConstructorVarMapper().getVar(paramInfo);
            }
            start = appendMethodArg(writer, var != null ? var : "null", start, breakLine, indent, indentNum + 1);
        }
        if (breakLine) {
            writer.println();
            Utils.printIndent(writer, indent, indentNum);
        }
        writer.print(")");
    }

    private void printConvertedVar(PrintWriter writer, Type converter, String sourceVar) {
        converter.startInvokeNew(writer, jpaContext.getViewContext());
        converter.endInvokeNew(writer);
        writer.print(".convert(");
        writer.print(sourceVar);
        writer.print(")");
    }

    public void printAssignmentInConstructor(PrintWriter writer, String indent, int indentNum) {
        if (ignore) {
            writer.print("null");
        } else if (propertyType == PropertyType.VIEW) {
            VarMapper varMapper = jpaContext.getConstructorVarMapper();
            writer.print(varMapper.getVar(target));
        } else if (propertyType == PropertyType.BASEABLE) {
            String sourceVar = getSourceVarInConstructor();
            Type converter = target.getConverter();
            if (converter != null && sourceVar != null) {
                printConvertedVar(writer, converter, sourceVar);
            } else {
                writer.print(sourceVar);
            }
        } else if (propertyType == PropertyType.EXTRACTOR) {
            printExtractor(writer, indent, indentNum);
        } else if (propertyType == PropertyType.EXTRA) {
            VarMapper varMapper = jpaContext.getConstructorVarMapper();
            writer.print(varMapper.getVar(target));
        }
    }

    private Type getViewType(Type from) {
        if (from.isView()) {
            return from;
        }
        if (from.isType(List.class) || from.isType(Set.class) || from.isType(Stack.class)) {
            return getViewType(from.getParameters().get(0));
        } else if (from.isType(Map.class)) {
            return getViewType(from.getParameters().get(1));
        } else if (from.isArray()) {
            Type componentType = from.getComponentType();
            assert componentType != null;
            return getViewType(componentType);
        } else {
            throw new IllegalArgumentException("Unsupported view collection type: " + from + ".");
        }
    }

    public void prepareConstructor(PrintWriter writer, String indent, int indentNum) {
        if (ignore) {
            return;
        }
        VarMapper varMapper = jpaContext.getConstructorVarMapper();
        if (propertyType == PropertyType.VIEW) {
            if (useReaderMethod) {
                String tempVar = varMapper.getVar(target, "viewVar", true);
                jpaContext.startAssignVar(writer, target.getType(), tempVar, indent, indentNum);
                getViewType(target.getType()).printType(writer, jpaContext.getViewContext(), false, false);
                writer.print(".read(");
                List<Property> extraProperties = subContext.getExtraProperties();
                Map<String, ParamInfo> extraParams = subContext.getExtraParams();
                boolean breakLine = extraProperties.size() + extraParams.size() > 5;
                appendMethodArg(writer, getSourceVarInConstructor(), true, breakLine, indent, indentNum + 1);
                for (Property extraProperty : extraProperties) {
                    String var = jpaContext.getConstructorVarMapper().getVar(extraProperty);
                    appendMethodArg(writer, var != null ? var : "null", false, breakLine, indent, indentNum + 1);
                }
                for (ParamInfo paramInfo : extraParams.values()) {
                    String var = jpaContext.getConstructorVarMapper().getVar(paramInfo);
                    appendMethodArg(writer, var != null ? var : "null", false, breakLine, indent, indentNum + 1);
                }
                if (breakLine) {
                    Utils.printIndent(writer, indent, indentNum);
                }
                writer.println(");");
            } else {
                for (PropertyData propertyData : viewPropertyData) {
                    propertyData.prepareConstructor(writer, indent, indentNum);
                }
                String tempVar = varMapper.getVar(target, "viewVar", true);
                jpaContext.startAssignVar(writer, target.getType(), tempVar, indent, indentNum);
                if (useFieldsConstructor) {
                    boolean start = true;
                    boolean breakLine = subContext.getProperties().stream().filter(p -> !p.isDynamic()).count() > 5;
                    if (constructNeedReflect) {
                        writer.print(getReflectUtilsType());
                        writer.println(".newInstance(");
                        Utils.printIndent(writer, indent, indentNum + 1);
                        writer.print(getReflectUtilsType());
                        writer.print(".getConstructor(");
                        for (Property property : subContext.getProperties()) {
                            if (!property.isDynamic()) {
                                start = Utils.appendMethodArg(writer, w -> {
                                    property.getType().printType(w, jpaContext.getViewContext(), false, false);
                                    w.print(".class");
                                }, start, breakLine, indent, indentNum + 2);
                            }
                        }
                        Utils.printIndent(writer, indent, indentNum + 1);
                        writer.print(")");
                        start = false;
                        breakLine = true;
                    } else {
                        target.getType().startInvokeNew(writer, jpaContext.getViewContext());
                    }
                    int i = 0;
                    for (Property property : subContext.getProperties()) {
                        if (!property.isDynamic()) {
                            int pos = Helper.findViewPropertyData(viewPropertyData, property, i);
                            PropertyData propertyData = pos != -1 ? viewPropertyData.get(i++) : null;
                            start = Utils.appendMethodArg(writer, (w) -> {
                                if (propertyData != null) {
                                    propertyData.printAssignmentInConstructor(w, indent, indentNum + 2);
                                } else {
                                    w.print("null");
                                }
                            }, start, breakLine, indent, indentNum + 1);
                        }
                    }
                    target.getType().endInvokeNew(writer);
                    writer.println(";");
                } else {
                    if (constructNeedReflect) {
                        writer.print(getReflectUtilsType());
                        writer.print(".newInstance(");
                        target.printType(writer, jpaContext.getViewContext(), false, false);
                        writer.println(".class);");
                    } else {
                        target.getType().startInvokeNew(writer, jpaContext.getViewContext());
                        target.getType().endInvokeNew(writer);
                        writer.println(";");
                    }
                    int i = 0;
                    for (Property property : subContext.getProperties()) {
                        if (!property.isDynamic()) {
                            jpaContext.startAssignVar(writer, null, tempVar + "." + subContext.getMappedFieldName(property), indent, indentNum);
                            int pos = Helper.findViewPropertyData(viewPropertyData, property, i);
                            PropertyData propertyData = pos != -1 ? viewPropertyData.get(i++) : null;
                            if (propertyData != null) {
                                propertyData.printAssignmentInConstructor(writer, indent, indentNum + 1);
                            } else {
                                writer.print("null");
                            }
                            writer.println(";");
                        }
                    }
                }
            }
        } else if (propertyType == PropertyType.EXTRACTOR) {
            if (parent == null || !parent.useReaderMethod) {
                if (extractor.useBeanProvider()) {
                    String configVar = varMapper.getVar(context.getConfigType().getQualifiedName(), "configVar", true);
                    jpaContext.startAssignVar(writer, context.getConfigType(), configVar, indent, indentNum);
                    jpaContext.getViewContext().printBeanProviderGetInstance(writer, context.getConfigType(), BeanUsage.CONFIGURE, "this", false);
                    writer.println(";");
                }
            }
        }
    }

    public Property getTarget() {
        return target;
    }
}
