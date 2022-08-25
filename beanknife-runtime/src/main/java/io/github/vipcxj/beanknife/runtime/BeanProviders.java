package io.github.vipcxj.beanknife.runtime;

import io.github.vipcxj.beanknife.runtime.providers.DefaultBeanProvider;
import io.github.vipcxj.beanknife.runtime.spi.BeanProvider;
import io.github.vipcxj.beanknife.runtime.utils.BeanUsage;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public enum BeanProviders {

    INSTANCE;

    private final ServiceLoader<BeanProvider> loader;
    private final Map<String, WeakReference<?>> cacheMap;

    BeanProviders() {
        loader = ServiceLoader.load(BeanProvider.class);
        cacheMap = new ConcurrentHashMap<>();
    }

    public  <T> T get(Class<T> type, BeanUsage usage, Object requester, boolean useDefaultBeanProvider, boolean cache) {
        String key = null;
        if (cache) {
            key = type.getName() + "_" + usage.name();
            WeakReference<?> reference = cacheMap.get(key);
            if (reference != null) {
                Object inst = reference.get();
                if (inst != null) {
                    //noinspection unchecked
                    return (T) inst;
                }
                // Don't remove invalid reference here. it will be replaced next step. Remove it here may cause concurrent error.
            }
        }
        int priority = Integer.MIN_VALUE;
        T instance = null;
        List<Throwable> suppressed = new ArrayList<>();
        Throwable throwable = null;
        for (BeanProvider provider : loader) {
            if (support(provider, type, usage, useDefaultBeanProvider)) {
                T inst;
                try {
                    inst = provider.get(type, usage, requester);
                } catch (Throwable t) {
                    inst = null;
                    if (throwable != null) {
                        suppressed.add(throwable);
                    }
                    throwable = t;
                }
                if (inst != null) {
                    if (provider.getPriority() > priority) {
                        priority = provider.getPriority();
                        instance = inst;
                    }
                }
            }
        }
        if (instance == null) {
            RuntimeException e = new RuntimeException("Unable to initialize the class: " + type.getName() + " for " + usage + " usage.");
            for (Throwable t : suppressed) {
                e.addSuppressed(t);
            }
            throw e;
        }
        if (cache) {
            cacheMap.put(key, new WeakReference<>(instance));
        }
        return instance;
    }

    private <T> boolean support(BeanProvider provider, Class<T> type, BeanUsage usage, boolean useDefaultBeanProvider) {
        try {
            if (provider instanceof DefaultBeanProvider && !useDefaultBeanProvider) {
                return false;
            }
            return provider.support(type, usage);
        } catch (Throwable t) {
            return false;
        }
    }
}
