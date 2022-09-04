package io.github.vipcxj.beanknife.runtime.annotations;

import io.github.vipcxj.beanknife.runtime.utils.Self;

import java.lang.annotation.*;

/**
 * Used to generate the meta class. However even not use it,
 * {@link ViewOf} will generate the meta class as well.
 * But if you want to change the simple name or package name of the generated class, this is your best choose.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewMetas.class)
public @interface ViewMeta {
    /**
     * The simple name of the generated class . By default, (the simple name if the original Class + "Meta") is used.
     * @return the target class
     */
    String value() default "";

    /**
     * The package of the generated class. Bu default, the package fo the original class is used.
     * @return the package of the generated.
     */
    String packageName() default "";
    Class<?> of() default Self.class;
}
