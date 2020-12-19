package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.OverrideViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

import java.util.*;

@ViewOf(value = ViewPropertyContainerBean.class, includePattern = ".*")
public class ViewPropertyContainerBeanViewConfig {

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

}
