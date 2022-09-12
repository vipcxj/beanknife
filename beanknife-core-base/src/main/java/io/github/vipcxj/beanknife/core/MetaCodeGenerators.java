package io.github.vipcxj.beanknife.core;

import io.github.vipcxj.beanknife.core.spi.MetaCodeGenerator;
import io.github.vipcxj.beanknife.core.utils.ResourceFinder;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public enum MetaCodeGenerators {

    INSTANCE;

    private final List<MetaCodeGenerator> generators;

    MetaCodeGenerators() {
        this.generators = new ArrayList<>();
        ResourceFinder finder = new ResourceFinder("META-INF/services/", ViewMetaProcessor.class.getClassLoader());
        try {
            List<Class<? extends MetaCodeGenerator>> implementations = finder.findAllImplementations(MetaCodeGenerator.class);
            for (Class<? extends MetaCodeGenerator> implementation : implementations) {
                generators.add(implementation.getDeclaredConstructor().newInstance());
            }
        } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public List<MetaCodeGenerator> getGenerators() {
        return generators;
    }
}
