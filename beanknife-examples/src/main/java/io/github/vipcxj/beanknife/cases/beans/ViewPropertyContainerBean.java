package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.annotations.ViewMeta;

import java.util.List;

@ViewMeta
public class ViewPropertyContainerBean {

    private long a;
    private String b;
    private ViewPropertyBean aProperty;
    private List<ViewPropertyBean> children;

    public long getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public ViewPropertyBean getaProperty() {
        return aProperty;
    }

    public List<ViewPropertyBean> getChildren() {
        return children;
    }
}
