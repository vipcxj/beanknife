package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewMeta;

@ViewMeta
public class MethodHideFieldBean {

    public String test;

    private String getTest() {
        return test;
    }
}
