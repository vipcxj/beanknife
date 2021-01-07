package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ready to use in next release.
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface MapViewProperty {
    /**
     * The property name. Should be unique in the generated class.
     * @return the property name
     */
    String name();
    /**
     * The property name being mapped. It should be one of the original's properties.
     * @return The property name being mapped
     */
    String map();
    /**
     * The access type of the getter methods. By default, inherited from the {@link ViewOf} annotation.
     * @return the access type of the getter methods
     */
    Access getter() default Access.UNKNOWN;
    /**
     * The access type of the setter methods. By default, inherited from the {@link ViewOf} annotation.
     * @return the access type of the setter methods
     */
    Access setter() default Access.UNKNOWN;
}
