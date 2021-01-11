package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.OverrideViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ViewOf(value = ViewPropertyWithExtraBean.class)
public class ViewPropertyWithExtraContainerBeanViewConfig extends IncludeAllBaseConfigure {
    @OverrideViewProperty(ViewPropertyWithExtraBeanMeta.a)
    private ExtraParamsBeanView a;
    @OverrideViewProperty(ViewPropertyWithExtraBeanMeta.b)
    private Map<String, List<ExtraProperties1BeanView[]>> b;
    @OverrideViewProperty(ViewPropertyWithExtraBeanMeta.c)
    private Set<ExtraProperties2BeanView> c;
}
