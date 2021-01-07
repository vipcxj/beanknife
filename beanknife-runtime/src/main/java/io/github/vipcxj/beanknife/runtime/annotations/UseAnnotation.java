package io.github.vipcxj.beanknife.runtime.annotations;

import io.github.vipcxj.beanknife.runtime.utils.AnnotationDest;
import io.github.vipcxj.beanknife.runtime.utils.AnnotationSource;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
@Repeatable(UseAnnotations.class)
@Inherited
public @interface UseAnnotation {
    Class<? extends Annotation>[] value() default {};
    AnnotationSource[] from() default { AnnotationSource.CONFIG, AnnotationSource.TARGET_TYPE, AnnotationSource.TARGET_GETTER, AnnotationSource.TARGET_FIELD };
    AnnotationDest[] dest() default { AnnotationDest.SAME };
}
