package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to add the new property to the generate class.
 * It should be put on the method.
 * If {@link Dynamic} is also found on this method, it means this method is the dynamic property method.
 * If there is no {@link Dynamic} found on this method, it means this method is the static property method.<br/>
 * The static property method should look like this:<br/>
 * <pre>
 * public static PropertyType methodName(SourceType source)
 * </pre>
 * The dynamic property method should look like this:<br/>
 * <pre>
 * public static PropertyType methodName(@InjectProperty TypeOfPropertyA a, @InjectProperty TypeOfPropertyB b, ...)
 * </pre>
 * The static property is initialized in the constructor or read method, and has a related field in the generated class.
 * However the dynamic property is calculated in the getter method, and has not a related field in the generated class.
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface NewViewProperty {
    /**
     * The property name.
     * @return the property name.
     */
    String value();
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
