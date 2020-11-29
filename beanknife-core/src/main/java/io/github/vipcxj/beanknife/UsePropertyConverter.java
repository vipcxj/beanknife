package io.github.vipcxj.beanknife;

import java.lang.annotation.*;

@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.SOURCE)
@Repeatable(UsePropertyConverters.class)
public @interface UsePropertyConverter {
    Class<? extends PropertyConverter<?, ?>> value();
}
