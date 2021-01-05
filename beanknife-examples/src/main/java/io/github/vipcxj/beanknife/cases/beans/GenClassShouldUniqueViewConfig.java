package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = SimpleBean.class, genName = "SimpleBeanViewNotUnique", includePattern = ".*")
@ViewOf(value = SimpleBean.class, genName = "SimpleBeanViewNotUnique", includePattern = ".*")
public class GenClassShouldUniqueViewConfig {
}
