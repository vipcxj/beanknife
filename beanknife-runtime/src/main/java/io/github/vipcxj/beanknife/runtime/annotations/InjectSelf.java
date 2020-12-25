package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used in dynamic method property. Inject the 'this' object to the method parameter.
 * The generated class may not exist yet, however, referencing it in the config class is legal.
 * Although the compiler may complain. Ignored it and after compiled, all will be ok.
 * Or compile first, after the class generated, the compiler will stop complain.
 *
 * @see Dynamic
 * @see NewViewProperty
 * @see OverrideViewProperty
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
public @interface InjectSelf {
}
