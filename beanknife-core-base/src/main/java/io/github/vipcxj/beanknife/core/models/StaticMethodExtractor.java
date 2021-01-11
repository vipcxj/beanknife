package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.CollectionUtils;
import io.github.vipcxj.beanknife.core.utils.ParamInfo;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.core.utils.VarMapper;
import io.github.vipcxj.beanknife.runtime.annotations.ExtraParam;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StaticMethodExtractor implements Extractor {

    @NonNull
    private ViewContext context;
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
            if (isSourceParam(context, parameter)) {
                paramInfoList.add(ParamInfo.sourceParam(parameter));
            } else if (extraParam != null) {
                paramInfoList.add(ParamInfo.extraParam(parameter, extraParam.value()));
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
/*        if (!executableElement.getModifiers().contains(Modifier.STATIC)) {
            context.error("The static property method \"" + name + "\" should be static.");
            return false;
        }*/
        List<? extends VariableElement> parameters = executableElement.getParameters();
        String sign = "\"public static " + returnType + " " + name + "()\" or \"public static <S extends " + context.getTargetType() + "> " + returnType + " " + name + "(S " + " source)\"";
        if (parameters.size() > 1) {
            context.error("The static property method \"" +
                    name +
                    "\" has to many parameters. " +
                    "It should look like " +
                    sign + "."
            );
            return false;
        }
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

    private void printParameter(@NonNull PrintWriter writer, @NonNull ParamInfo paramInfo, @NonNull VarMapper varMapper) {
        if (paramInfo.isSource()) {
            writer.print("source");
        } else if (paramInfo.isExtraParam()) {
            writer.print(varMapper.getVar(paramInfo.getExtraParamName()));
        } else {
            writer.print("null");
        }
    }

    @Override
    public void print(PrintWriter writer, @CheckForNull VarMapper varMapper, @NonNull String indent, int indentNum) {
        if (varMapper == null) {
            throw new NullPointerException("This is impossible!");
        }
        printConfigBean(writer, "source");
        writer.print(".");
        writer.print(executableElement.getSimpleName());
        if (paramInfoList.size() < 5) {
            writer.print("(");
            int i = 0;
            for (ParamInfo paramInfo : paramInfoList) {
                printParameter(writer, paramInfo, varMapper);
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
                printParameter(writer, paramInfo, varMapper);
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
