package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewMeta;

@ViewMeta
public class InheritedBean extends MixedAllBean {

    private String a;
    private String newProperty;

    public String getNewProperty() {
        return newProperty;
    }

    @Override
    public String getIf() {
        return "if";
    }
}
