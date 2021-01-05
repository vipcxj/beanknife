package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = SimpleBean.class, genName = "SimpleBeanWithInvalidIncludePattern", includePattern = "\\")
public class InvalidPatternViewConfig {
}
