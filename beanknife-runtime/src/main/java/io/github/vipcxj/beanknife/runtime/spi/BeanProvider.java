package io.github.vipcxj.beanknife.runtime.spi;

import io.github.vipcxj.beanknife.runtime.utils.BeanUsage;

/**
 * Used to tell the library how to instantiate a bean.
 */
public interface BeanProvider {

    int DEFAULT_PRIORITY = 0;

    /**
     * The priority. The higher is selected than lower.
     * @return the priority
     */
    default int getPriority() {
        return DEFAULT_PRIORITY;
    }

    /**
     * Test whether the type is supported by this provider. If not sure or hard to check, just return true.
     * If {@link #get(Class, BeanUsage, Object)} return null, The library think this provider not support the type as well.
     * @param type the bean type
     * @param usage the bean usage
     * @return whether the type is supported by this provider.
     */
    boolean support(Class<?> type, BeanUsage usage);

    /**
     * create or get a instance of the type.
     * @param type the bean type
     * @param usage the bean usage
     * @param requester the bean request the instance.
     * @param <T> the bean type
     * @return the bean instance.
     */
    <T> T get(Class<T> type, BeanUsage usage, Object requester);
}
