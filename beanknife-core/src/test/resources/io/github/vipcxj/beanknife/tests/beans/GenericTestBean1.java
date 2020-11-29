package io.github.vipcxj.beanknife.tests.beans;

import io.github.vipcxj.beanknife.ViewOf;

import java.util.List;

@ViewOf
public class GenericTestBean1<T1, T2 extends Number> {

    private T1 a;
    private T2 b;
    private List<String> c;

    public T1 getA() {
        return a;
    }

    public T2 getB() {
        return b;
    }

    public List<String> getC() {
        return c;
    }
}