package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.annotations.ViewMeta;

@ViewMeta
public class IsPropertyBean {

    public boolean isMe;
    String isTest;

    public boolean isMe() {
        return isMe;
    }

    public String isTest() {
        return isTest;
    }
}
