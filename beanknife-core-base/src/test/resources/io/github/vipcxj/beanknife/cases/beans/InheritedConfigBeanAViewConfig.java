package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = BeanA.class, genName = "BeanAViewWithInheritedConfig", includePattern = ".*")
public class InheritedConfigBeanAViewConfig extends InheritedConfigBaseViewConfig {
}
