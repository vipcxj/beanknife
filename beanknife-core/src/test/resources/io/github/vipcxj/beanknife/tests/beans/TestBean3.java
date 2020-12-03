package io.github.vipcxj.beanknife.tests.beans;

import io.github.vipcxj.beanknife.annotations.Access;
import io.github.vipcxj.beanknife.annotations.ViewProperty;

import java.util.List;
import java.util.Set;

public class TestBean3 {

    private int a;
    protected long b;
    private boolean c;
    private List<TestBean3> beans1;
    private Set<TestBean3> beans2;

    public int getA() {
        return a;
    }

    @ViewProperty(setter = Access.DEFAULT)
    protected boolean isC() {
        return c;
    }

    void setC(boolean c) {
        this.c = c;
    }

    public List<TestBean3> getBeans1() {
        return beans1;
    }

    public void setBeans1(List<TestBean3> beans1) {
        this.beans1 = beans1;
    }

    public Set<TestBean3> getBeans2() {
        return beans2;
    }

    public void setBeans2(Set<TestBean3> beans2) {
        this.beans2 = beans2;
    }

    long getD1() {
        return a + b;
    }

    protected long getD2() {
        return a + b;
    }
}
