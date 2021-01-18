package io.github.vipcxj.beanknife.core;

import io.github.vipcxj.beanknife.core.spi.ViewCodeGenerator;
import io.github.vipcxj.beanknife.core.utils.ResourceFinder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public enum ViewCodeGenerators {

    INSTANCE;

    private List<ViewCodeGenerator> generators;

    ViewCodeGenerators() {
        this.generators = new ArrayList<>();
        ResourceFinder finder = new ResourceFinder("META-INF/services/", ViewOfProcessor.class.getClassLoader());
        try {
            List<Class<? extends ViewCodeGenerator>> implementations = finder.findAllImplementations(ViewCodeGenerator.class);
            for (Class<? extends ViewCodeGenerator> implementation : implementations) {
                generators.add(implementation.getDeclaredConstructor().newInstance());
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public List<ViewCodeGenerator> getGenerators() {
        return generators;
    }
}
