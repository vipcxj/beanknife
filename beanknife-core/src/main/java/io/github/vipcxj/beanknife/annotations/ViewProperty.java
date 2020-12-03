package io.github.vipcxj.beanknife.annotations;

public @interface ViewProperty {
    Access getter() default Access.PUBLIC;
    Access setter() default Access.NONE;
}
