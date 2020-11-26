package io.github.vipcxj.beanknife.tests.beans;

import io.github.vipcxj.beanknife.ViewMeta;

@ViewMeta
@ViewMeta(packageName = "io.github.vipcxj.beanknife.tests.otherbeans")
@ViewMeta("ViewOfTestBean1")
@ViewMeta(value = "ViewOfTestBean1", packageName = "io.github.vipcxj.beanknife.tests.otherbeans")
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

    long getD1() {
        return a + b;
    }

    protected long getD2() {
        return a + b;
    }
}
