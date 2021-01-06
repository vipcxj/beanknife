package io.github.vipcxj.beanknife.cases.beans;

public class BeanB { // (6)
    private String a;
    private BeanA beanA;
    private String shouldBeRemoved;

    public String getA() {
        return a;
    }
    public BeanA getBeanA() {
        return beanA;
    }
    public String getShouldBeRemoved() {
        return shouldBeRemoved;
    }
}
