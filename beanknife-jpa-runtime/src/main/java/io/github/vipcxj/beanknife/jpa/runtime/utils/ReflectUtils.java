package io.github.vipcxj.beanknife.jpa.runtime.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectUtils {

    public static <T> Constructor<T> getConstructor(Class<T> type, Class<?>... argTypes) {
        try {
            return type.getConstructor(argTypes);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Constructor<T> constructor, Object... args) {
        try {
            constructor.setAccessible(true);
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T newInstance(Class<T> type) {
        return newInstance(getConstructor(type));
    }

    public static <T> T getProperty(Object target, String propertyName, String getterName) {
        Class<?> clazz = target.getClass();
        if (getterName != null) {
            try {
                Method method = clazz.getMethod(getterName);
                method.setAccessible(true);
                //noinspection unchecked
                return (T) method.invoke(target);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Field clazzField = clazz.getField(propertyName);
                clazzField.setAccessible(true);
                //noinspection unchecked
                return (T) clazzField.get(target);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void setProperty(Object target, String field, Object value) {
        Class<?> clazz = target.getClass();
        try {
            Field clazzField = clazz.getField(field);
            clazzField.setAccessible(true);
            clazzField.set(target, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
