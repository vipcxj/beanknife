package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.AsView;
import io.github.vipcxj.beanknife.runtime.annotations.NewViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.OverrideViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = ViewPropertyContainerBean.class, includePattern = ".*")
public class ViewPropertyContainerBeanViewConfig {
/*    @NewViewProperty("newProperty")
    public GenericBeanView<String, Integer> newProperty() {
        return null;
    }

    @OverrideViewProperty(ViewPropertyContainerBeanMeta.aProperty)
    @AsView(ViewPropertyBeanMeta.Views.io_github_vipcxj_beanknife_cases_beans_ViewPropertyBeanWithoutParent)
    private ViewPropertyBeanWithoutParent aProperty;
    @OverrideViewProperty(ViewPropertyContainerBeanMeta.children)
    @AsView(ViewPropertyBeanMeta.Views.io_github_vipcxj_beanknife_cases_beans_ViewPropertyBeanWithoutParent)
    private List<ViewPropertyBeanWithoutParent> children;*/
}
