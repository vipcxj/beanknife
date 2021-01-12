package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.Access;
import io.github.vipcxj.beanknife.runtime.annotations.ViewPropertiesExclude;
import io.github.vipcxj.beanknife.runtime.annotations.ViewPropertiesInclude;
import io.github.vipcxj.beanknife.runtime.annotations.ViewSetters;

@ViewSetters(Access.NONE)
// Not work, because "a" is excluded by parent configuration.
@ViewPropertiesInclude("a")
@ViewPropertiesExclude("b")
public class Parent2ViewConfigure extends GrandparentViewConfigure {
}
