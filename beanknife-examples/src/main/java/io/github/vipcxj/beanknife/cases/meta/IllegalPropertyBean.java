package io.github.vipcxj.beanknife.cases.meta;

import io.github.vipcxj.beanknife.annotations.ViewMeta;

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
