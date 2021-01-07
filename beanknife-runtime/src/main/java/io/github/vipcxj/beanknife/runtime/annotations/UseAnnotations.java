package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface UseAnnotations {
    UseAnnotation[] value() default {};
}
