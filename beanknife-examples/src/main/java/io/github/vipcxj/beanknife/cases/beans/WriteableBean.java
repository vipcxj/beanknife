package io.github.vipcxj.beanknife.cases.beans;

import java.util.List;
import java.util.Set;

public class WriteableBean<T> {
    private String a;
    private boolean b;
    private List<? extends Set<? extends T>> c;
    List<SimpleBean> d;
    Integer e;
    private long f;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public boolean isB() {
        return b;
    }

    public void setB(boolean b) {
        this.b = b;
    }

    public List<? extends Set<? extends T>> getC() {
        return c;
    }

    public void setC(List<? extends Set<? extends T>> c) {
        this.c = c;
    }

    public List<SimpleBean> getD() {
        return d;
    }

    public void setD(List<SimpleBean> d) {
        this.d = d;
    }

    public long getF() {
        return f;
    }

    public void setF(long f) {
        this.f = f;
    }
}
