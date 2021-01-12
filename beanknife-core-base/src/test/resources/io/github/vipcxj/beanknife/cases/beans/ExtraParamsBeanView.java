package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = SimpleBean.class, configClass = ExtraParamsViewConfigure.class)
public class ExtraParamsBeanView {

    private String a;

    private Integer b;

    private long c;

    private Class<?> x;

    private String y;

    private ExtraParamsBeanView z;

    public ExtraParamsBeanView() { }

    public ExtraParamsBeanView(
        String a,
        Integer b,
        long c,
        Class<?> x,
        String y,
        ExtraParamsBeanView z
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public ExtraParamsBeanView(ExtraParamsBeanView source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
        this.x = source.x;
        this.y = source.y;
        this.z = source.z;
    }

    public ExtraParamsBeanView(SimpleBean source, Class<?> x, ExtraParamsBeanView z) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.ExtraParamsBeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
        this.x = ExtraParamsViewConfigure.x(x);
        this.y = ExtraParamsViewConfigure.y(x);
        this.z = ExtraParamsViewConfigure.z(z);
    }

    public static ExtraParamsBeanView read(SimpleBean source, Class<?> x, ExtraParamsBeanView z) {
        if (source == null) {
            return null;
        }
        return new ExtraParamsBeanView(source, x, z);
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

    public Class<?> getX() {
        return this.x;
    }

    public String getY() {
        return this.y;
    }

    public ExtraParamsBeanView getZ() {
        return this.z;
    }

}
