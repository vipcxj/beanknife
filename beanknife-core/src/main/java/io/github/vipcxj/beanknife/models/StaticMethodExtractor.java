package io.github.vipcxj.beanknife.models;

import javax.lang.model.element.ExecutableElement;
import java.io.PrintWriter;

public class StaticMethodExtractor implements Extractor {

    private Type container;
    private ExecutableElement executableElement;

    @Override
    public void print(PrintWriter writer, Context context) {
        container.printType(writer, context, false, false);
        writer.print(".");
        writer.print(executableElement.getSimpleName());
        writer.print("(source)");
    }
}
