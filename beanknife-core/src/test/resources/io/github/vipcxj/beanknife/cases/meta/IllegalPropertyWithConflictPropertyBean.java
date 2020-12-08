package io.github.vipcxj.beanknife.cases.meta;

import io.github.vipcxj.beanknife.annotations.ViewMeta;

@ViewMeta
public class IllegalPropertyWithConflictPropertyBean {

    private String _while;

    public String getIf_() {
        return getIf();
    }

    public String getIf() {
        return "if";
    }

    public String getIf__() {
        return getIf_();
    }

    public String getWhile() {
        return _while;
    }
}
