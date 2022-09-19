package io.github.vipcxj.beanknife.runtime.annotations;

import java.lang.annotation.*;

/**
 * The mapper of original class name to generated meta class name.<br/>
 * For example:<br/>
 * <code>@ViewMetaNameMapper("${name}Meta")</code><br/>
 * means the class Bean will generate meta class BeanMeta, the class Apple will generate class AppleMeta.<br/>
 * <code>@ViewMetaNameMapper("${config%Configure}Meta")</code>
 * means the config class BeanConfigure will generate class BeanMeta, the config class AppleConfigure will generate class AppleMeta.<br/>
 * The expression in '${}' works like shell variable.
 * Support ${VarName#ShortFrontToRemove}, ${VarName##LongFrontToRemove}, ${VarName%ShortFrontToRemove}, ${VarName%%LongFrontToRemove}.<br/>
 * 'VarName' can be 'name' (the original class simple name), 'config' (the config class simple name).
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Inherited
public @interface ViewMetaNameMapper {
    String value();
}
