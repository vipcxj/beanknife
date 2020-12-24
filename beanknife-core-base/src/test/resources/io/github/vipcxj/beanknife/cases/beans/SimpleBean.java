package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(includePattern=".*") // (1)
public class SimpleBean {
    private String a;
    private Integer b;
    private long c;

    public SimpleBean(String a, Integer b, long c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public String getA() {
        return a;
    }

    public Integer getB() {
        return b;
    }

    public long getC() {
        return c;
    }
}