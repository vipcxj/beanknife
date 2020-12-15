package io.github.vipcxj.beanknife.runtime.annotations;

import io.github.vipcxj.beanknife.runtime.PropertyConverter;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UsePropertyConverters.class)
public @interface UsePropertyConverter {
    Class<? extends PropertyConverter<?, ?>> value();
}
