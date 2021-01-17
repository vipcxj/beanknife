package io.github.vipcxj.beanknife.core.spi;

import io.github.vipcxj.beanknife.core.models.ViewContext;

import java.io.PrintWriter;

public interface ViewCodeGenerator {

    void ready(ViewContext context);

    void print(PrintWriter writer, ViewContext context, String indent, int indentNum);
}
