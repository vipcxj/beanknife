package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.annotations.RemoveViewProperty;
import io.github.vipcxj.beanknife.annotations.ViewOf;

@ViewOf(value = ViewPropertyBean.class, genName = "ViewPropertyBeanWithoutParent", includePattern = ".*")
@RemoveViewProperty(ViewPropertyBeanMeta.parent)
public class ViewPropertyBeanViewConfig {
}
