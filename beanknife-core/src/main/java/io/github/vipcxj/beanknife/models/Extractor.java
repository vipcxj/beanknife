package io.github.vipcxj.beanknife.models;

import java.io.PrintWriter;

public interface Extractor {

    void print(PrintWriter writer, Context context);
}
