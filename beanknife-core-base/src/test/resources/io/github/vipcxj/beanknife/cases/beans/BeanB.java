package io.github.vipcxj.beanknife.cases.beans;

public class BeanB { // (6)
    private String a;
    private BeanA beanA;
    private BeanA anotherBeanA;
    private String shouldBeRemoved;

    public String getA() {
        return a;
    }
    public BeanA getBeanA() {
        return beanA;
    }
    public BeanA getAnotherBeanA() {
        return anotherBeanA;
    }
    public String getShouldBeRemoved() {
        return shouldBeRemoved;
    }
}
