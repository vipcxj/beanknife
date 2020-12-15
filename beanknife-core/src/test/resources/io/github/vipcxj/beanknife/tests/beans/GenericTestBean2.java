package io.github.vipcxj.beanknife.tests.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewMeta;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

import java.util.List;
import java.util.Set;

@ViewOf(includePattern = ".*")
public class GenericTestBean2<T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> {

    private T1 a;
    private T2 b;

    public T1 getA() {
        return a;
    }

    public T2 getB() {
        return b;
    }

    @ViewMeta
    static class NestTestBean1<T3> {
        private T3 c;

        public T3 getC() {
            return c;
        }
    }
}
