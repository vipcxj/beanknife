package io.github.vipcxj.beanknife.runtime.spi;

public interface BeanProvider {

    boolean support(Class<?> type);
    <T> T get(Class<T> type);
}
