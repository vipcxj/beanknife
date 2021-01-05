package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.Access;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = SimpleBean.class, genName = "SimpleBeanWithDefaultSetters", includePattern = ".*", setters = Access.DEFAULT)
@ViewOf(value = SimpleBean.class, genName = "SimpleBeanWithPrivateSetters", includePattern = ".*", setters = Access.PRIVATE)
@ViewOf(value = SimpleBean.class, genName = "SimpleBeanWithProtectedSetters", includePattern = ".*", setters = Access.PROTECTED)
@ViewOf(value = SimpleBean.class, genName = "SimpleBeanWithDefaultSetters", includePattern = ".*", setters = Access.PUBLIC)
@ViewOf(value = SimpleBean.class, genName = "SimpleBeanWithUnknownSetters", includePattern = ".*", setters = Access.UNKNOWN)
public class SetterAccessViewConfig {
}
