package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.annotations.ViewOf;

@ViewOf(value = MetaAndViewOfOnDiffBean1.class, includePattern = ".*", excludes = {MetaAndViewOfOnDiffBean1Meta.pa})
public class MetaAndViewOfOnDiffBean1ViewConfig {
}
