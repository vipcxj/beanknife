package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.io.PrintWriter;

public interface Extractor {

    boolean check(@NonNull ViewContext context, @CheckForNull Property property);
    Type getReturnType();
    void print(PrintWriter writer, Context context);
    boolean isDynamic();
}
