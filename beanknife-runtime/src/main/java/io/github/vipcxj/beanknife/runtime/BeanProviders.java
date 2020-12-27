package io.github.vipcxj.beanknife.runtime;

import io.github.vipcxj.beanknife.runtime.spi.BeanProvider;

import java.util.ServiceLoader;

public enum BeanProviders {

    INSTANCE;

    BeanProviders() {
        ServiceLoader<BeanProvider> loader = ServiceLoader.load(BeanProvider.class);
    }
}
