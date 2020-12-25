package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.NonNull;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class StaticMethodExtractor implements Extractor {

    @NonNull
    private final Type container;
    @NonNull
    private final ExecutableElement executableElement;
    @NonNull
    private final Type returnType;

    public StaticMethodExtractor(@NonNull Context context, @NonNull Type container, @NonNull ExecutableElement executableElement) {
        this.container = container;
        this.executableElement = executableElement;
        Type type = Type.extract(context, executableElement);
        if (type == null) {
            context.error("Failed to resolve the return type of property method" + executableElement.getSimpleName() + ".");
            this.returnType = Type.extract(context, Object.class);
        } else {
            this.returnType = type;
        }
    }

    @Override
    public boolean check(@NonNull ViewContext context) {
        Name name = executableElement.getSimpleName();
        if (!executableElement.getModifiers().contains(Modifier.STATIC)) {
            context.error("The static property method \"" + name + "\" should be static.");
            return false;
        }
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
        if (parameters.size() == 1) {
            Type paramType = Type.extract(context, parameters.get(0));
            List<Type> testTypes = new ArrayList<>();
            if (paramType.isTypeVar()) {
                testTypes.addAll(paramType.getUpperBounds());
                if (testTypes.isEmpty()) {
                    testTypes.add(Type.extract(context, Object.class));
                }
            } else {
                testTypes.add(paramType);
            }
            if (testTypes.stream().anyMatch(testType -> !context.getTargetType().canAssignTo(testType))) {
                context.error("The static property method \"" +
                        name +
                        "\" has wrong parameter. " +
                        "It should look like " +
                        sign + "."
                );
                return false;
            }
        }
        return true;
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

    @Override
    public void print(PrintWriter writer, Context context) {
        container.printType(writer, context, false, false);
        writer.print(".");
        writer.print(executableElement.getSimpleName());
        if (executableElement.getParameters().isEmpty()) {
            writer.print("()");
        } else {
            writer.print("(source)");
        }
    }
}
