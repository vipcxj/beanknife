package io.github.vipcxj.beanknife.beans;

import io.github.vipcxj.beanknife.BeanKnife;

@BeanKnife
public class TestBean1 {

    private int a;
    private long b;
    private boolean c;

    public int getA() {
        return a;
    }

    public boolean isC() {
        return c;
    }
}
