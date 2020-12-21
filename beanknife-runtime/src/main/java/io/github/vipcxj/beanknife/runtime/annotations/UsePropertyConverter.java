package io.github.vipcxj.beanknife.runtime.annotations;

import io.github.vipcxj.beanknife.runtime.PropertyConverter;

import java.lang.annotation.*;

/**
 * Used to specialize the converter. Multi converters are supported. The annotation process will select a suitable one from them.
 */
@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(UsePropertyConverters.class)
public @interface UsePropertyConverter {
    /**
     * The converter type.
     * @return the converter type.
     */
    Class<? extends PropertyConverter<?, ?>> value();
}
