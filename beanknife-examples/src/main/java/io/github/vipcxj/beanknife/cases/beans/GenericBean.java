package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.annotations.ViewMeta;

import java.util.List;

@ViewMeta
public class GenericBean<T1, T2 extends Number> {

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