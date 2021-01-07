package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(UnUseAnnotations.class)
@Inherited
public @interface UnUseAnnotation {
    Class<? extends Annotation>[] value() default {};
}
