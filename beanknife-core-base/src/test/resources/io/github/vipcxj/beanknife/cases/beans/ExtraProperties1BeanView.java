package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = SimpleBean.class, configClass = ExtraProperty1ViewConfigure.class)
public class ExtraProperties1BeanView {

    private String a;

    private Integer b;

    private long c;

    private List<Date> x;

    public ExtraProperties1BeanView() { }

    public ExtraProperties1BeanView(
        String a,
        Integer b,
        long c,
        List<Date> x
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.x = x;
    }

    public ExtraProperties1BeanView(ExtraProperties1BeanView source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
        this.x = source.x;
    }

    public ExtraProperties1BeanView(SimpleBean source, List<Date> x) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.ExtraProperties1BeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
        this.x = x;
    }

    public static ExtraProperties1BeanView read(SimpleBean source, List<Date> x) {
        if (source == null) {
            return null;
        }
        return new ExtraProperties1BeanView(source, x);
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

    public List<Date> getX() {
        return this.x;
    }

}
