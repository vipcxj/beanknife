package io.github.vipcxj.beanknife.models;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintWriter;

public interface Extractor {

    boolean check(@Nonnull ViewContext context, @Nullable Property property);
    Type getReturnType();
    void print(PrintWriter writer, Context context);
    boolean isDynamic();
}
