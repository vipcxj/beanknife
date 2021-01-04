package io.github.vipcxj.beanknife.spring;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BeanUtils implements ApplicationContextAware  {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        BeanUtils.context = applicationContext;
    }

    public static  <T> T getBean(Class<T> type) {
        if (context == null) {
            throw new IllegalStateException("The application context is not initialized yet.");
        }
        return context.getBean(type);
    }
}
