package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Change a exist property. <br/>
 * If put on a field, the type of the field should be convertible from the original type.<br/>
 * Here are three cases:<br/>
 * 1. The field type can be assigned by the original type.<br/>
 * 2. Specialize a suitable converter to manually convert the original type to the field type.<br/>
 * 3. The field type is the DTO version of the original type.
 * Or the field type is the Array, List, Set, Stack, Map of DTO version of the original type and their composition, and they share the same shape.
 *  For example: BeanDTO vs Bean,
 *  List&lt;BeanDTO&gt; vs List&lt;Bean&gt;,
 *  Map&lt;String, Set&lt;BeanDTO&gt; vs Map&lt;String, Set&lt;Bean&gt;
 *
 * @see NewViewProperty
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.SOURCE)
public @interface OverrideViewProperty {
    /**
     * The original property name.
     * @return The original property name.
     */
    String value();

    /**
     * The access type of the getter methods.
     * By default, inherited from the {@link ViewOf} or {@link ViewProperty} annotation.
     * @return the access type of the getter methods
     */
    Access getter() default Access.UNKNOWN;
    /**
     * The access type of the setter methods.
     * By default, inherited from the {@link ViewOf} or {@link ViewProperty} annotation.
     * @return the access type of the setter methods
     */
    Access setter() default Access.UNKNOWN;
}
