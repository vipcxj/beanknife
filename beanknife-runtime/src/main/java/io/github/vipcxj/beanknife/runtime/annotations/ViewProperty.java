package io.github.vipcxj.beanknife.runtime.annotations;

/**
 * The same as {@link OverrideViewProperty},
 * But it is only used in the original class, so no value attribute to specialize the property name.
 * @see OverrideViewProperty
 */
public @interface ViewProperty {
    /**
     * The access type of the getter methods. By default, public is used.
     * It can be override by the {@link ViewProperty}, {@link OverrideViewProperty} and {@link NewViewProperty}.
     * @return the access type of the getter methods
     */
    Access getter() default Access.PUBLIC;
    /**
     * The access type of the setter methods. By default, none is used, which means there are no setter method.
     * It can be override by the {@link ViewProperty}, {@link OverrideViewProperty} and {@link NewViewProperty}.
     * @return the access type of the setter methods
     */
    Access setter() default Access.NONE;
}
