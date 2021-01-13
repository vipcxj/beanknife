package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = SimpleBean.class, configClass = ExtraProperty2ViewConfigure.class)
public class ExtraProperties2BeanView {

    private String a;

    private Integer b;

    private long c;

    private String y;

    private Class<? extends String> z;

    public ExtraProperties2BeanView() { }

    public ExtraProperties2BeanView(
        String a,
        Integer b,
        long c,
        String y,
        Class<? extends String> z
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.y = y;
        this.z = z;
    }

    public ExtraProperties2BeanView(ExtraProperties2BeanView source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
        this.y = source.y;
        this.z = source.z;
    }

    public ExtraProperties2BeanView(SimpleBean source, String y, Class<? extends String> z) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.ExtraProperties2BeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
        this.y = y;
        this.z = z;
    }

    public static ExtraProperties2BeanView read(SimpleBean source, String y, Class<? extends String> z) {
        if (source == null) {
            return null;
        }
        return new ExtraProperties2BeanView(source, y, z);
    }

    public String getA() {
        return this.a;
    }

    public Integer getB() {
        return this.b;
    }

    public long getC() {
        return this.c;
    }

    public String getY() {
        return this.y;
    }

    public Class<? extends String> getZ() {
        return this.z;
    }

}
