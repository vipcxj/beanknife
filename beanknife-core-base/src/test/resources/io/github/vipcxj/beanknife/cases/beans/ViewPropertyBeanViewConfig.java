package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.*;

@ViewOf(value = ViewPropertyBean.class, genName = "ViewPropertyBeanWithoutParent", includePattern = ".*")
@RemoveViewProperty(ViewPropertyBeanMeta.parent)
@ViewWriteBackMethod(Access.PUBLIC)
@ViewCreateAndWriteBackMethod(Access.PUBLIC)
public class ViewPropertyBeanViewConfig {
}
