package io.github.vipcxj.beanknife.cases.meta;

import io.github.vipcxj.beanknife.annotations.ViewMeta;

@ViewMeta
public class MethodHideFieldBean {

    public String test;

    private String getTest() {
        return test;
    }
}
