package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

/**
 * The mapper of original class name to generated class name.<br/>
 * For example:<br/>
 * <code>@ViewGenNameMapper("${name}Dto")</code><br/>
 * means the class Bean will generate class BeanDto, the class Apple will generate class AppleDto.<br/>
 * <code>@ViewGenNameMapper("${config%Configure}Dto")</code>
 * means the config class BeanConfigure will generate class BeanDto, the config class AppleConfigure will generate class AppleDto.<br/>
 * The expression in '${}' works like shell variable.
 * Support ${VarName#ShortFrontToRemove}, ${VarName##LongFrontToRemove}, ${VarName%ShortFrontToRemove}, ${VarName%%LongFrontToRemove}.<br/>
 * 'VarName' can be 'name' (the original class simple name), 'config' (the config class simple name).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface ViewGenNameMapper {
    String value();
}
