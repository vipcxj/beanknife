package io.github.vipcxj.beanknife.runtime.annotations;

import io.github.vipcxj.beanknife.runtime.utils.Self;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewOfs.class)
public @interface ViewOf {
    Class<?> value() default Self.class;
    Class<?> config() default Self.class;
    String genPackage() default "";
    String genName() default "";
    Access access() default Access.PUBLIC;
    String[] includes() default {};
    String[] excludes() default {};
    String includePattern() default "";
    String excludePattern() default "";
    Access emptyConstructor() default Access.PUBLIC;
    Access fieldsConstructor() default Access.PUBLIC;
    Access copyConstructor() default Access.PUBLIC;
    Access getters() default Access.PUBLIC;
    Access setters() default Access.NONE;
    boolean errorMethods() default true;
}
