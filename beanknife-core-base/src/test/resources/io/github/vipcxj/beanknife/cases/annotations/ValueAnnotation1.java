package io.github.vipcxj.beanknife.cases.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface ValueAnnotation1 {
    Class<?>[] type() default ValueAnnotation1.class;
    ValueAnnotation2 annotation() default @ValueAnnotation2;
    ValueAnnotation2[] annotations() default {};
}
