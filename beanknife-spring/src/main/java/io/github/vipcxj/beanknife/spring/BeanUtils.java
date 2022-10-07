package io.github.vipcxj.beanknife.spring;

import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BeanUtils implements ApplicationContextAware  {

    private static ApplicationContext context;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        BeanUtils.context = applicationContext;
    }

    private static int calcDist(Class<?> child, Class<?> base) {
        if (Objects.equals(child, base)) {
            return 0;
        }
        if (base.isInterface()) {
            return 1;
        }
        if (Objects.equals(child, Object.class)) {
            return -1;
        }
        Class<?> superclass = child.getSuperclass();
        if (superclass == null) {
            return -1;
        }
        int dist = calcDist(superclass, base);
        if (dist == -1) {
            return -1;
        } else {
            return 1 + dist;
        }
    }

    public static <T> T getBean(Class<T> type) {
        if (context == null) {
            throw new IllegalStateException("The application context is not initialized yet.");
        }
        Map<String, T> beans = context.getBeansOfType(type);
        if (beans.isEmpty()) {
            throw new NoSuchBeanDefinitionException(type);
        }
        if (beans.size() == 1) {
            return beans.values().iterator().next();
        }
        int minDist = Integer.MAX_VALUE;
        T matched = null;
        List<String> matchedBeanNames = new ArrayList<>();

        for (Map.Entry<String, T> entry : beans.entrySet()) {
            String key = entry.getKey();
            T value = entry.getValue();
            if (value == null) {
                continue;
            }
            int dist = calcDist(value.getClass(), type);
            if (dist == -1) {
                continue;
            }
            if (dist < minDist) {
                matched = value;
                matchedBeanNames.clear();
                matchedBeanNames.add(key);
                minDist = dist;
            } else if (dist == minDist) {
                matchedBeanNames.add(key);
            }
        }
        if (matchedBeanNames.isEmpty()) {
            throw new NoSuchBeanDefinitionException(type);
        } else if (matchedBeanNames.size() > 1) {
            throw new NoUniqueBeanDefinitionException(type, matchedBeanNames);
        } else {
            return matched;
        }
    }
}
