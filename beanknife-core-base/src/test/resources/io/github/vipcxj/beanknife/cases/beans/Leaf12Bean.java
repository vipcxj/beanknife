package io.github.vipcxj.beanknife.cases.beans;

import java.lang.annotation.Annotation;
import java.util.List;

public class Leaf12Bean {
    private int a;
    private Class<? extends Annotation> b;
    private List<? extends String> c;
    private long d;
    private String e;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public Class<? extends Annotation> getB() {
        return b;
    }

    public void setB(Class<? extends Annotation> b) {
        this.b = b;
    }

    public List<? extends String> getC() {
        return c;
    }

    public void setC(List<? extends String> c) {
        this.c = c;
    }

    public long getD() {
        return d;
    }

    public void setD(long d) {
        this.d = d;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }
}
