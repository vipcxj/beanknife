package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The property annotated by this annotation is the dynamic property,
 * which means the property value is calculated when the getter method is called.
 * In other words, there is no really field in the generated class,
 * the property value is calculated on the fly.<br/>
 * The method annotated by this annotation should has the shape as following: <br/>
 * <pre>
 * public static PropertyType methodName(@InjectProperty TypeOfPropertyA a, @InjectProperty TypeOfPropertyB b, ...)
 * <pre/>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Dynamic {
}
