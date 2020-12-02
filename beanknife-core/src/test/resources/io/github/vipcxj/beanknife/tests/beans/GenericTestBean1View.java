package io.github.vipcxj.beanknife.tests.beans;

import java.util.List;

public class GenericTestBean1View<T1, T2 extends Number> {

    private T1 a;
    private T2 b;
    private List<String> c;

    public GenericTestBean1View() {}

    public GenericTestBean1View(
            T1 a,
            T2 b,
            List<String> c
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public GenericTestBean1View(GenericTestBean1View<T1, T2> source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
    }

    public static <T1, T2 extends Number> GenericTestBean1View<T1, T2> read(GenericTestBean1<T1, T2> source) {
        GenericTestBean1View<T1, T2> out = new GenericTestBean1View<>();
        out.a = source.getA();
        out.b = source.getB();
        out.c = source.getC();
        return out;
    }

    public T1 getA() {
        return this.a;
    }

    public T2 getB() {
        return this.b;
    }

    public List<String> getC() {
        return this.c;
    }
}