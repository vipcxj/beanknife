package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(includes = {SerializableBeanMeta.a, SerializableBeanMeta.b}, serializable = true, serialVersionUID = 4L)
public class SerializableBean {
    private String a;
    private long b;

    public String getA() {
        return a;
    }

    public long getB() {
        return b;
    }
}
