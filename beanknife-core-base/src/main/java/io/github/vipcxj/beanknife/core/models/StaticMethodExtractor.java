package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.CollectionUtils;
import io.github.vipcxj.beanknife.core.utils.ParamInfo;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.core.utils.VarMapper;
import io.github.vipcxj.beanknife.runtime.annotations.ExtraParam;
import io.github.vipcxj.beanknife.runtime.annotations.InjectProperty;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class StaticMethodExtractor implements Extractor {

    @NonNull
    private final ViewContext context;
    @CheckForNull
    private final Type container;
    @NonNull
    private final ExecutableElement executableElement;
    @NonNull
    private final Type returnType;
    private final List<ParamInfo> paramInfoList;

    public StaticMethodExtractor(@NonNull ViewContext context, @NonNull ExecutableElement executableElement) {
        this.context = context;
        this.container = Type.extract(context, context.getViewOf().getConfigElement());
        this.executableElement = executableElement;
        Type type = Type.extract(context, executableElement);
        if (type == null) {
            context.error("Failed to resolve the return type of property method" + executableElement.getSimpleName() + ".");
            this.returnType = Type.extract(context, Object.class);
        } else {
            this.returnType = type;
        }
        this.paramInfoList = collectParamInfoList(context, executableElement);
    }

    private static List<ParamInfo> collectParamInfoList(ViewContext context, @NonNull ExecutableElement executableElement) {
        List<ParamInfo> paramInfoList = new ArrayList<>();
        for (VariableElement parameter : executableElement.getParameters()) {
            ExtraParam extraParam = parameter.getAnnotation(ExtraParam.class);
            InjectProperty injectProperty = parameter.getAnnotation(InjectProperty.class);
            if (extraParam != null && injectProperty != null) {
                context.error("@ExtraParam and @InjectProperty can not be put on the same parameter: " + executableElement.getSimpleName() + "#" + parameter.getSimpleName() + ". The @ExtraParam is ignored.");
            }
            if (injectProperty != null) {
                Property injectedProperty = context.getBaseProperties().stream().filter(property -> property.getName().equals(injectProperty.value())).findAny().orElse(null);
                if (injectedProperty == null) {
                    context.error("Unable to inject the property " +
                            injectProperty.value() + ", No property named " +
                            injectProperty.value() + " in the original class " + context.getTargetType() + "."
                    );
                    paramInfoList.add(ParamInfo.unknown(parameter));
                } else {
                    paramInfoList.add(ParamInfo.propertyParam(parameter, injectedProperty));
                }
            } else if (extraParam != null) {
                paramInfoList.add(ParamInfo.extraParam(parameter, extraParam.value()));
            } else if (isSourceParam(context, parameter)) {
                paramInfoList.add(ParamInfo.sourceParam(parameter));
            } else {
                paramInfoList.add(ParamInfo.unknown(parameter));
            }
        }
        return paramInfoList;
    }

    private static boolean isSourceParam(@NonNull ViewContext context, @NonNull VariableElement parameter) {
        Type paramType = Type.extract(context, parameter);
        List<Type> testTypes = new ArrayList<>();
        if (paramType.isTypeVar()) {
            testTypes.addAll(paramType.getUpperBounds());
            if (testTypes.isEmpty()) {
                testTypes.add(Type.extract(context, Object.class));
            }
        } else {
            testTypes.add(paramType);
        }
        return testTypes.stream().allMatch(testType -> context.getTargetType().canAssignTo(testType));
    }

    @Override
    public boolean check() {
        if (container == null) {
            context.error("Unable to resolve the type config element: " + context.getViewOf().getConfigElement().getQualifiedName());
            return false;
        }
        Name name = executableElement.getSimpleName();
        if (paramInfoList.stream().filter(ParamInfo::isSource).count() > 1) {
            String params = paramInfoList.stream().filter(ParamInfo::isSource).map(ParamInfo::getParameterName).collect(Collectors.joining(", "));
            context.error("There should be at most one source type parameter in the static property method \"" + name + "\". No there are many: " + params + ".");
            return false;
        }
        if (paramInfoList.stream().anyMatch(ParamInfo::isUnknown)) {
            String params = paramInfoList.stream().filter(ParamInfo::isUnknown).map(ParamInfo::getParameterName).collect(Collectors.joining(", "));
            context.error("There are some unknown parameter in the static property method \"" + name + "\": " + params + ".");
            return false;
        }
        List<List<ParamInfo>> checkUnique = CollectionUtils.checkUnique(paramInfoList.stream().filter(ParamInfo::isExtraParam).collect(Collectors.toList()), Comparator.comparing(ParamInfo::getExtraParamName));
        if (!checkUnique.isEmpty()) {
            for (List<ParamInfo> infos : checkUnique) {
                String params = infos.stream().map(ParamInfo::getParameterName).collect(Collectors.joining(", "));
                context.error("" +
                        "There are multi extra parameters with same name \"" +
                        infos.get(0).getExtraParamName() +
                        "\" in the static property method \"" + name + "\". " +
                        "They are " + params + ".");
            }
            return false;
        }
        for (ParamInfo paramInfo : paramInfoList) {
            if (paramInfo.isPropertyParam()) {
                Property injectedProperty = paramInfo.getInjectedProperty();
                Type type = Type.extract(context, paramInfo.getVar());
                if (!injectedProperty.getType().canAssignTo(type)) {
                    context.error("The type of the inject property parameter " +
                            paramInfo.getParameterName() +
                            " in the static property method " +
                            name +
                            " is illegal. The binding property " +
                            injectedProperty.getName() +
                            " can not be injected into it."
                    );
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    @NonNull
    public ViewContext getContext() {
        return context;
    }

    @Override
    @CheckForNull
    public Type getContainer() {
        return container;
    }

    @Override
    @NonNull
    public ExecutableElement getExecutableElement() {
        return executableElement;
    }

    @Override
    @NonNull
    public Type getReturnType() {
        return returnType;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    public List<ParamInfo> getParamInfoList() {
        return paramInfoList;
    }

    private void printParameter(@NonNull PrintWriter writer, @NonNull ParamInfo paramInfo, @NonNull VarMapper varMapper, boolean useSource) {
        if (paramInfo.isSource()) {
            writer.print("source");
        } else if (paramInfo.isExtraParam()) {
            Object key = Objects.requireNonNull(getParamInfoKey(paramInfo));
            writer.print(varMapper.getVar(key, paramInfo.getExtraParamName()));
        } else if (paramInfo.isPropertyParam()) {
            Property injectedProperty = paramInfo.getInjectedProperty();
            if (useSource) {
                writer.print(injectedProperty.getOriginalValueString("source"));
            } else {
                writer.print(varMapper.getVar(injectedProperty, injectedProperty.getName()));
            }
        } else {
            writer.print("null");
        }
    }

    private Object getParamInfoKey(ParamInfo info) {
        if (info.isExtraParam()) {
            return context.getExtraParams().get(info.getExtraParamName());
        } else {
            return null;
        }
    }

    private void printConfigBean(PrintWriter writer, @NonNull String requester) {
        if (getContainer() == null) {
            throw new IllegalStateException("This is impossible!");
        }
        if (getExecutableElement().getModifiers().contains(Modifier.STATIC)) {
            getContainer().printType(writer, context, false, false);
        } else {
            getContext().printInitConfigureBean(writer, requester, true);
        }
    }

    public void print(PrintWriter writer, @CheckForNull VarMapper varMapper, boolean useSource, @NonNull String indent, int indentNum) {
        if (varMapper == null) {
            throw new NullPointerException("This is impossible!");
        }
        printConfigBean(writer, useSource ? "source" : "this");
        writer.print(".");
        writer.print(executableElement.getSimpleName());
        if (paramInfoList.size() < 5) {
            writer.print("(");
            int i = 0;
            for (ParamInfo paramInfo : paramInfoList) {
                printParameter(writer, paramInfo, varMapper, useSource);
                if (i++ != paramInfoList.size() - 1) {
                    writer.print(", ");
                }
            }
            writer.print(")");
        } else {
            writer.print("(");
            int i = 0;
            for (ParamInfo paramInfo : paramInfoList) {
                writer.println();
                Utils.printIndent(writer, indent, indentNum + 1);
                printParameter(writer, paramInfo, varMapper, useSource);
                if (i++ != paramInfoList.size() - 1) {
                    writer.print(", ");
                }
            }
            writer.println();
            Utils.printIndent(writer, indent, indentNum);
            writer.print(")");
        }
    }
}
