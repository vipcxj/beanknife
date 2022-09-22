package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewMeta;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@ViewMeta
public class ViewPropertyContainerBean {

    private long a;
    private String b;
    private ViewPropertyBean view;
    private ViewPropertyBean[] viewArray;
    private List<ViewPropertyBean> viewList;
    private Set<ViewPropertyBean> viewSet;
    private Map<String, ViewPropertyBean> viewMap;
    private List<Map<String, ViewPropertyBean>> viewMapList;
    private Map<String, List<ViewPropertyBean>> viewListMap;
    private Map<String, List<Map<Integer, Stack<ViewPropertyBean>>>> viewStackMapListMap;
    private Map<String, List<Map<Integer, Stack<ViewPropertyBean>>>>[][][] viewStackMapListMapArrayArrayArray;

    public long getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public ViewPropertyBean getView() {
        return view;
    }

    public void setView(ViewPropertyBean view) {
        this.view = view;
    }

    public List<ViewPropertyBean> getViewList() {
        return viewList;
    }

    public void setViewList(List<ViewPropertyBean> viewList) {
        this.viewList = viewList;
    }

    public ViewPropertyBean[] getViewArray() {
        return viewArray;
    }

    public Set<ViewPropertyBean> getViewSet() {
        return viewSet;
    }

    public Map<String, ViewPropertyBean> getViewMap() {
        return viewMap;
    }

    public List<Map<String, ViewPropertyBean>> getViewMapList() {
        return viewMapList;
    }

    public Map<String, List<ViewPropertyBean>> getViewListMap() {
        return viewListMap;
    }

    public Map<String, List<Map<Integer, Stack<ViewPropertyBean>>>> getViewStackMapListMap() {
        return viewStackMapListMap;
    }

    public Map<String, List<Map<Integer, Stack<ViewPropertyBean>>>>[][][] getViewStackMapListMapArrayArrayArray() {
        return viewStackMapListMapArrayArrayArray;
    }
}
