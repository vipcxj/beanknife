package io.github.vipcxj.beanknife.spring;

import io.github.vipcxj.beanknife.runtime.spi.BeanProvider;
import io.github.vipcxj.beanknife.runtime.utils.BeanUsage;

public class SpringBeanProvider implements BeanProvider {

    public int getPriority() {
        return -100;
    }

    public boolean support(Class<?> type, BeanUsage usage) {
        return usage == BeanUsage.CONFIGURE;
    }

    public <T> T get(Class<T> type, BeanUsage usage, Object requester) {
        return BeanUtils.getBean(type);
    }
}
