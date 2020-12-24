package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewMeta;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

import java.util.Date;

@ViewMeta
@ViewOf(includes = {MetaAndViewOfBothOnBeanMeta.a, MetaAndViewOfBothOnBeanMeta.c, MetaAndViewOfBothOnBeanMeta.e})
public class MetaAndViewOfBothOnBean {
    private int a;
    private long b;
    private String c;
    private short d;
    private Date e;

    public int getA() {
        return a;
    }

    public void setA(int a) {
        this.a = a;
    }

    public long getB() {
        return b;
    }

    public void setB(long b) {
        this.b = b;
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public short getD() {
        return d;
    }

    public void setD(short d) {
        this.d = d;
    }

    public Date getE() {
        return e;
    }

    public void setE(Date e) {
        this.e = e;
    }
}
