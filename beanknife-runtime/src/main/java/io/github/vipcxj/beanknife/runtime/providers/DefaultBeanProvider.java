package io.github.vipcxj.beanknife.runtime.providers;

import io.github.vipcxj.beanknife.runtime.spi.BeanProvider;
import io.github.vipcxj.beanknife.runtime.utils.BeanUsage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DefaultBeanProvider implements BeanProvider {

    @Override
    public int getPriority() {
        return Integer.MIN_VALUE + 1;
    }

    @Override
    public boolean support(Class<?> type, BeanUsage usage) {
        return true;
    }

    @Override
    public <T> T get(Class<T> type, BeanUsage usage, Object requester) {
        try {
            Constructor<T> constructor = type.getConstructor();
            return constructor.newInstance();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            return null;
        }
    }
}
