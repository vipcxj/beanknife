package io.github.vipcxj.beanknife.cases.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
public @interface ValueAnnotation2 {
    String value() default "";
}
