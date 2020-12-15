package io.github.vipcxj.beanknife.runtime.annotations;

public @interface ViewProperty {
    Access getter() default Access.PUBLIC;
    Access setter() default Access.NONE;
}
