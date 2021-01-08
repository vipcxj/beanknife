package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

/**
 * The mapper of original class name to generated class name.<br/>
 * For example:<br/>
 * <code>@ViewGenNameMapper("${name}Dto")</code><br/>
 * means the class Bean will generate class BeanDto, the class Apple will generate class AppleDto.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface ViewGenNameMapper {
    String value();
}
