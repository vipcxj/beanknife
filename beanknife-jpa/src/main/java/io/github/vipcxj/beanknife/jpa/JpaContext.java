package io.github.vipcxj.beanknife.jpa;

import io.github.vipcxj.beanknife.core.models.Property;
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
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

public class JpaContext {
    public static final String TYPE_ENTITY = "javax.persistence.Entity";
    public static final String TYPE_EMBEDDABLE = "javax.persistence.Embeddable";
    public static final String TYPE_CRITERIA_BUILDER = "javax.persistence.criteria.CriteriaBuilder";
    public static final String SIMPLE_TYPE_CRITERIA_BUILDER = "CriteriaBuilder";
    public static final String TYPE_SELECTION = "javax.persistence.criteria.Selection";
    public static final String SIMPLE_TYPE_SELECTION = "Selection";
    public static final String TYPE_FROM = "javax.persistence.criteria.From";
    public static final String TYPE_REFLECT_UTILS = "io.github.vipcxj.beanknife.jpa.runtime.utils.ReflectUtils";
    public static final String SIMPLE_TYPE_REFLECT_UTILS = "ReflectUtils";
    public static final String SIMPLE_TYPE_FROM = "From";
    private static final String INIT_ARG_SOURCE = "source";
    private static final String PREVENT_CONFLICT_ARG_KEY = "prevent conflict arg";
    private static final String SOURCE_ARG_KEY = "source arg";
    private static final String TYPE_ADD_JPA_SUPPORT = "io.github.vipcxj.beanknife.jpa.runtime.annotations.AddJpaSupport";
    private final ViewContext viewContext;
    private boolean enabled;
    private boolean fixConstructor;
    private boolean provideSource;
    private boolean canUseReader;
    private long argsNum;
    private String preventConflictArgVar = "preventConflictArg";
    private final VarMapper constructorVarMapper;
    private VarMapper constructorArgsVarMapper;
    private final VarMapper selectionMethodVarMapper;
    private List<PropertyData> propertyDataList;

    public JpaContext(ViewContext viewContext) {
        this.viewContext = viewContext;
        this.constructorVarMapper = new VarMapper();
        this.selectionMethodVarMapper = new VarMapper();
        init();
    }

    private void init() {
        Types typeUtils = viewContext.getProcessingEnv().getTypeUtils();
        TypeElement targetElement = viewContext.getViewOf().getTargetElement();
        TypeElement configElement = viewContext.getViewOf().getConfigElement();
        Elements elementUtils = viewContext.getProcessingEnv().getElementUtils();
        List<AnnotationMirror> annotations = Utils.getAnnotationsOn(elementUtils, configElement, TYPE_ADD_JPA_SUPPORT, null, true, true);
        if (!annotations.isEmpty()) {
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
        if (enabled) {
            viewContext.importVariable(TYPE_SELECTION, SIMPLE_TYPE_SELECTION);
            viewContext.importVariable(TYPE_CRITERIA_BUILDER, SIMPLE_TYPE_CRITERIA_BUILDER);
            viewContext.importVariable(TYPE_FROM, SIMPLE_TYPE_FROM);
            propertyDataList = PropertyData.collectData(this);
            if (provideSource) {
                constructorVarMapper.getVar(SOURCE_ARG_KEY, INIT_ARG_SOURCE);
            }
            canUseReader = viewContext.canSeeReadConstructor(viewContext.getPackageName()) && provideSource && propertyDataList
                    .stream()
                    .noneMatch(data -> data.isIgnore() && data.getPropertyType() != PropertyType.BASEABLE && data.getPropertyType() != PropertyType.EXTRA);
            // 最终properties里就三种Property, baseProperty，viewProperty，extraProperty
            checkConstructor();
        }
    }

    public static boolean unSupportType(Type type) {
        return type.getQualifiedName().equals("java.util.List")
                || type.getQualifiedName().equals("java.util.Set")
                || type.getQualifiedName().equals("java.util.Collection")
                || type.getQualifiedName().equals("java.util.Map")
                || type.isArray();
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

    private void checkConstructor() {
        long fieldsCount = viewContext.getProperties().size() - viewContext.getProperties().stream().filter(Property::isDynamic).count();
        argsNum = constructorVarMapper.getKeys().size();
        List<Type> types = constructorVarMapper.getKeys()
                .stream()
                .map(key -> {
                    if (key instanceof Property) {
                        return ((Property) key).getType();
                    } else if (key instanceof ParamInfo) {
                        return Type.extract(viewContext, ((ParamInfo) key).getVar());
                    } else if (key.equals(SOURCE_ARG_KEY)) {
                        return viewContext.getTargetType();
                    } else {
                        throw new IllegalArgumentException("This is impossible!");
                    }
                }).collect(Collectors.toList());
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
        } else {
            fixConstructor = false;
        }
        if (fixConstructor) {
            argsNum += 1;
            this.preventConflictArgVar = constructorVarMapper.getVar(PREVENT_CONFLICT_ARG_KEY, preventConflictArgVar);
        }
        constructorArgsVarMapper = new VarMapper(constructorVarMapper);
    }

    public VarMapper getConstructorVarMapper() {
        return constructorVarMapper;
    }

    public VarMapper getSelectionMethodVarMapper() {
        return selectionMethodVarMapper;
    }

    public void importReflect() {
        getViewContext().importVariable(TYPE_REFLECT_UTILS, SIMPLE_TYPE_REFLECT_UTILS);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getPreventConflictArgVar() {
        return preventConflictArgVar;
    }

    public boolean isProvideSource() {
        return provideSource;
    }

    public boolean isCanUseReader() {
        return canUseReader;
    }

    public void markProvideSource() {
        this.provideSource = true;
    }

    public boolean isFixConstructor() {
        return fixConstructor;
    }

    public long getArgsNum() {
        return argsNum;
    }

    public ViewContext getViewContext() {
        return viewContext;
    }

    public void startAssignVar(PrintWriter writer, Type type, String var, String indent, int indentNum) {
        Utils.printIndent(writer, indent, indentNum);
        if (type != null) {
            type.printType(writer, getViewContext(), true, false);
            writer.print(" ");
        }
        writer.print(var);
        writer.print(" = ");
    }

    public void printConstructor(PrintWriter writer, String indent) {
        boolean breakLine = constructorArgsVarMapper.getKeys().size() > 5;
        Utils.printIndent(writer, indent, 1);
        writer.print("public ");
        writer.print(viewContext.getGenType().getSimpleName());
        writer.print(" (");
        boolean start = true;
        for (Object key : constructorArgsVarMapper.getKeys()) {
            start = Utils.appendMethodArg(writer, w -> {
                if (key instanceof Property) {
                    Property property = (Property) key;
                    property.getType().printType(w, viewContext, true, false);
                } else if (key instanceof ParamInfo) {
                    ParamInfo paramInfo = (ParamInfo) key;
                    Type type = Type.extract(viewContext, paramInfo.getVar());
                    if (type == null) {
                        w.print("Object");
                    } else {
                        type.printType(w, viewContext, true, false);
                    }
                } else if (key.equals(SOURCE_ARG_KEY)) {
                    viewContext.getTargetType().printType(w, viewContext, true, false);
                } else if (key.equals(PREVENT_CONFLICT_ARG_KEY)) {
                    w.print("int");
                } else {
                    w.print("Object");
                }
                w.print(" ");
                w.print(constructorArgsVarMapper.getVar(key));
            }, start, breakLine, indent, 2);
        }
        if (breakLine) {
            writer.println();
            Utils.printIndent(writer, indent, 1);
        }
        writer.println(") {");
        for (PropertyData propertyData : propertyDataList) {
            propertyData.prepareConstructor(writer, indent, 2);
        }
        int i = 0;
        for (Property property : viewContext.getProperties()) {
            if (!property.isDynamic()) {
                int pos = Helper.findViewPropertyData(propertyDataList, property, i);
                PropertyData propertyData = pos != -1 ? propertyDataList.get(i++) : null;
                if (propertyData != null) {
                    startAssignVar(writer, null, "this." + viewContext.getMappedFieldName(property), indent, 2);
                    propertyData.printAssignmentInConstructor(writer, indent, 2);
                    writer.println(";");
                }
            }
        }
        Utils.printIndent(writer, indent, 1);
        writer.println("}");
        writer.println();
    }

}
