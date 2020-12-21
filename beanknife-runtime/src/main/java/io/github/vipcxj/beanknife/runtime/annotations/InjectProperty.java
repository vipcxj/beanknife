package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to inject the property to the dynamic property method.
 * @see Dynamic
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface InjectProperty {
    String value() default "";
}
