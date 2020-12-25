package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = BeanB.class, genName = "BeanBViewWithInheritedConfig", includePattern = ".*")
public class InheritedConfigBeanBViewConfig extends InheritedConfigBaseViewConfig {
}
