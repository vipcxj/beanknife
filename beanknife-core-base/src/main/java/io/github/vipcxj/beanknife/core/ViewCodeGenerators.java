package io.github.vipcxj.beanknife.core;

import io.github.vipcxj.beanknife.core.spi.ViewCodeGenerator;

import java.util.ServiceLoader;

public enum ViewCodeGenerators {

    INSTANCE;

    private final ServiceLoader<ViewCodeGenerator> loader;

    ViewCodeGenerators() {
        loader = ServiceLoader.load(ViewCodeGenerator.class);
    }

    public ServiceLoader<ViewCodeGenerator> getGenerators() {
        return loader;
    }
}
