package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.converters.StringToLongConverter;
import io.github.vipcxj.beanknife.runtime.annotations.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@ViewOf(value = ViewPropertyContainerBean.class, includePattern = ".*")
@ViewPropertiesIncludePattern(".*")
@ViewWriteBackMethod(Access.PUBLIC)
@ViewCreateAndWriteBackMethod(Access.PUBLIC)
public class ViewPropertyContainerBeanViewConfig {

    @OverrideViewProperty("b")
    @UsePropertyConverter(StringToLongConverter.class)
    private long b;
    @OverrideViewProperty(ViewPropertyContainerBeanMeta.view)
    private ViewPropertyBeanWithoutParent view;
    @OverrideViewProperty(ViewPropertyContainerBeanMeta.viewArray)
    private ViewPropertyBeanWithoutParent[] viewArray;
    @OverrideViewProperty(ViewPropertyContainerBeanMeta.viewList)
    private List<ViewPropertyBeanWithoutParent> viewList;
    @OverrideViewProperty(ViewPropertyContainerBeanMeta.viewSet)
    private Set<ViewPropertyBeanWithoutParent> viewSet;
    @OverrideViewProperty(ViewPropertyContainerBeanMeta.viewMap)
    private Map<String, ViewPropertyBeanWithoutParent> viewMap;
    @OverrideViewProperty(ViewPropertyContainerBeanMeta.viewMapList)
    private List<Map<String, ViewPropertyBeanWithoutParent>> viewMapList;
    @OverrideViewProperty(ViewPropertyContainerBeanMeta.viewListMap)
    private Map<String, List<ViewPropertyBeanWithoutParent>> viewListMap;
    @OverrideViewProperty(ViewPropertyContainerBeanMeta.viewStackMapListMap)
    private Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>> viewStackMapListMap;
    @OverrideViewProperty(ViewPropertyContainerBeanMeta.viewStackMapListMapArrayArrayArray)
    private Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>>[][][] viewStackMapListMapArrayArrayArray;

    @NewViewProperty("newIntFieldProperty")
    private int newIntFieldProperty;

    @NewViewProperty("newIntMethodProperty")
    public int newIntMethodProperty() {
        return 0;
    }
}
