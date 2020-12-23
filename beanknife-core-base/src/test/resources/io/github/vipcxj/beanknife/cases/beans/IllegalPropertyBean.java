package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewMeta;

@ViewMeta
public class IllegalPropertyBean {

    private String _while;

    public String getIf() {
        return "if";
    }

    public String getWhile() {
        return _while;
    }
}
