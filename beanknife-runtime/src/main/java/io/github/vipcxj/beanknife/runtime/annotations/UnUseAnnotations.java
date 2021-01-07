package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface UnUseAnnotations {
    UnUseAnnotation[] value() default {};
}
