package io.github.vipcxj.beanknife.tests.beans;

import java.util.List;
import java.util.Set;

public class GenericTestBean2View<T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> {

    private T1 a;
    private T2 b;

    public GenericTestBean2View() {}

    public GenericTestBean2View(
            T1 a,
            T2 b
    ) {
        this.a = a;
        this.b = b;
    }

    public GenericTestBean2View(GenericTestBean2View<T1, T2> source) {
        this.a = source.a;
        this.b = source.b;
    }

    public static <T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> GenericTestBean2View<T1, T2> read(GenericTestBean2<T1, T2> source) {
        GenericTestBean2View<T1, T2> out = new GenericTestBean2View<>();
        out.a = source.getA();
        out.b = source.getB();
        return out;
    }

    public T1 getA() {
        return this.a;
    }

    public T2 getB() {
        return this.b;
    }

}
