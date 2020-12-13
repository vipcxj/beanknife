package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.annotations.ViewMeta;

@ViewMeta
public class ViewPropertyBean {

    private int a;
    private String b;
    private ViewPropertyContainerBean parent;

    public int getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public ViewPropertyContainerBean getParent() {
        return parent;
    }
}
