package io.github.vipcxj.beanknife.tests.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewMeta;

import java.util.Set;

@ViewMeta
@ViewMeta(packageName = "io.github.vipcxj.beanknife.tests.otherbeans")
@ViewMeta("ViewOfTestBean1")
@ViewMeta(value = "ViewOfTestBean1", packageName = "io.github.vipcxj.beanknife.tests.otherbeans")
public class TestBean1 {

    private Set<? extends Set<String>> n1;
    private Set<Set<String>> n2;
    private Set<String> n3;
    private Set n4;
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

    @ViewMeta
    public static class NestTestBean1 {
        private int a;

        public int getA() {
            return a;
        }
    }
}
