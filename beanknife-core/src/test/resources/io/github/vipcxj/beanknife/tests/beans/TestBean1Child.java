package io.github.vipcxj.beanknife.tests.beans;

import io.github.vipcxj.beanknife.annotations.ViewMeta;

import java.util.List;

@ViewMeta
public class TestBean1Child extends TestBean1 {

    public int a;
    private short d;
    private List<Integer> e;

    public short getD() {
        return d;
    }

    public List<Integer> getE() {
        return e;
    }
}
