package io.github.vipcxj.beanknife.tests.beans;

import io.github.vipcxj.beanknife.ViewMeta;
import io.github.vipcxj.beanknife.ViewOf;

import java.util.List;

@ViewMeta
@ViewMeta(packageName = "io.github.vipcxj.beanknife.tests.otherbeans")
@ViewMeta("ViewOfTestBean2")
@ViewMeta(value = "ViewOfTestBean2", packageName = "io.github.vipcxj.beanknife.tests.otherbeans")
public class TestBean2<T1 extends CharSequence & Number, T2 extends List<String>> {

    private T1 a;
    private T2 b;

    public T1 getA() {
        return a;
    }

    public T2 getB() {
        return b;
    }

    @ViewMeta
    static class TestBeanChild1<T3> {
        private T3 c;

        public T3 getC() {
            return c;
        }
    }
}
