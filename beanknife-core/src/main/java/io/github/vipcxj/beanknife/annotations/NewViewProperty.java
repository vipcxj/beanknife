package io.github.vipcxj.beanknife.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface NewViewProperty {
    String value();
    Access getter() default Access.UNKNOWN;
    Access setter() default Access.UNKNOWN;
}
