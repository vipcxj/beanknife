package io.github.vipcxj.beanknife.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface CreateViewProperty {
    String value() default "";
    InheritableAccess getter() default InheritableAccess.INHERITED;
    InheritableAccess setter() default InheritableAccess.INHERITED;
}
