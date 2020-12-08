package io.github.vipcxj.beanknife.models;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.PrintWriter;

public class DirectExtractor implements Extractor {

    @Override
    public boolean check(@Nonnull ViewContext context, @Nullable Property property) {
        return false;
    }

    @Override
    public Type getReturnType() {
        return null;
    }

    @Override
    public void print(PrintWriter writer, Context context) {

    }

    @Override
    public boolean isDynamic() {
        return false;
    }
}
