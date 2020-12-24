package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewMeta;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

import java.util.List;
import java.util.Map;
import java.util.Set;

@ViewMeta
@ViewOf(includes = {GenericBeanMeta.a, GenericBeanMeta.b, GenericBeanMeta.c, GenericBeanMeta.d, GenericBeanMeta.e})
public class GenericBean<T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> {

    private T1 a;
    private T2 b;
    private List<T1> c;
    private T2[] d;
    private List<Map<String, Map<T1, T2>>[]>[] e;

    public T1 getA() {
        return a;
    }

    public T2 getB() {
        return b;
    }

    public List<T1> getC() {
        return c;
    }

    public T2[] getD() {
        return d;
    }

    public List<Map<String, Map<T1, T2>>[]>[] getE() {
        return e;
    }
}