package io.github.vipcxj.beanknife.tests.beans;

import io.github.vipcxj.beanknife.ViewOf;

@ViewOf(includePattern = "*", excludes = {"d1"})
public class TestBean3 {

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
