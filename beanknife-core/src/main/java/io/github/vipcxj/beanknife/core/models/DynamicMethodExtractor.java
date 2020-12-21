package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.runtime.annotations.InjectProperty;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;
import java.io.PrintWriter;
import java.util.List;

public class DynamicMethodExtractor implements Extractor {

    @NonNull
    private final Type container;
    @NonNull
    private final ExecutableElement executableElement;
    @NonNull
    private final Type returnType;

    public DynamicMethodExtractor(@NonNull Context context, @NonNull Type container, @NonNull ExecutableElement executableElement) {
        this.container = container;
        this.executableElement = executableElement;
        Type type = Type.extract(context, executableElement, null);
        if (type == null) {
            context.error("Failed to resolve the return type of property method" + executableElement.getSimpleName() + ".");
            this.returnType = Type.extract(context, Object.class);
        } else {
            this.returnType = type;
        }
    }

    @Override
    public boolean check(@NonNull ViewContext context, @CheckForNull Property property) {
        Name name = executableElement.getSimpleName();
        if (!executableElement.getModifiers().contains(Modifier.STATIC)) {
            context.error("The dynamic property method \"" + name + "\" should be static.");
            return false;
        }
        if (property != null && !returnType.equals(property.getType())) {
            context.error("The dynamic property method \"" + name + "\" should return a \"" + returnType + "\" type.");
            return false;
        }
        List<? extends VariableElement> parameters = executableElement.getParameters();
        for (VariableElement parameter : parameters) {
            InjectProperty injectProperty = parameter.getAnnotation(InjectProperty.class);
            if (injectProperty == null) {
                context.error("All parameters of the dynamic property method \"" + name + "\" should annotated with the annotation @InjectProperty.");
                return false;
            }
            String propertyName = !injectProperty.value().isEmpty() ? injectProperty.value() : parameter.getSimpleName().toString();
            Property ip = context.getProperties().stream().filter(p -> p.getName().equals(propertyName)).findAny().orElse(null);
            if (ip == null) {
                context.error("Unable to inject the property " +
                        propertyName + " to the dynamic property method \""
                        + name + "\"." +
                        " The property does not exists.");
                return false;
            }
            if (!context.getProcessingEnv().getTypeUtils().isAssignable(ip.getTypeMirror(), parameter.asType())) {
                context.error("Unable to inject the property " +
                        propertyName + " to the dynamic property method \""
                        + name + "\"." +
                        " The property type mismatched.");
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
        return true;
    }

    @Override
    public void print(PrintWriter writer, Context context) {
        container.printType(writer, context, false, false);
        writer.print(".");
        writer.print(executableElement.getSimpleName());
        writer.print("(");
        int i = 0;
        List<? extends VariableElement> parameters = executableElement.getParameters();
        for (VariableElement parameter : parameters) {
            InjectProperty injectProperty = parameter.getAnnotation(InjectProperty.class);
            if (injectProperty != null) {
                String propertyName = !injectProperty.value().isEmpty() ? injectProperty.value() : parameter.getSimpleName().toString();
                writer.print("this.");
                writer.print(context.getMappedFieldName(propertyName));
            } else {
                writer.print("null");
            }
            if (i++ != parameters.size() - 1) {
                writer.print(", ");
            }
        }
        writer.print(")");
    }
}
