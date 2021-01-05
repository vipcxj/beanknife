package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.Access;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = SimpleBean.class, genName = "SimpleBeanWithDefaultGetters", includePattern = ".*", getters = Access.DEFAULT)
@ViewOf(value = SimpleBean.class, genName = "SimpleBeanWithPrivateGetters", includePattern = ".*", getters = Access.PRIVATE)
@ViewOf(value = SimpleBean.class, genName = "SimpleBeanWithProtectedGetters", includePattern = ".*", getters = Access.PROTECTED)
@ViewOf(value = SimpleBean.class, genName = "SimpleBeanWithUnknownGetters", includePattern = ".*", getters = Access.UNKNOWN)
@ViewOf(value = SimpleBean.class, genName = "SimpleBeanWithoutGetters", includePattern = ".*", getters = Access.NONE)
public class GetterAccessViewConfig {
}
