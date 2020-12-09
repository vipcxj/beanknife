package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.annotations.ViewOf;

@ViewOf(includePattern = ".*")
public class ViewOfDirectOnBean {

    private long a;
    private String b;

    public long getA() {
        return a;
    }

    public String getB() {
        return b;
    }
}
