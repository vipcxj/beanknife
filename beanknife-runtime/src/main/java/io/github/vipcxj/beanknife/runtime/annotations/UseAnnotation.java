package io.github.vipcxj.beanknife.runtime.annotations;

import io.github.vipcxj.beanknife.runtime.utils.AnnotationPos;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(UseAnnotations.class)
@Inherited
public @interface UseAnnotation {
    Class<? extends Annotation>[] value() default {};
    boolean useFromTarget() default true;
    boolean useFromConfig() default true;
    AnnotationPos[] dest() default { AnnotationPos.SAME };
}
