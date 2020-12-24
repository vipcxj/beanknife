package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

import java.util.List;
import java.util.Set;

@ViewOf(includePattern = ".*")
public class NestedGenericBean<T1 extends CharSequence & Set<? extends Character>, T2 extends List<? extends Set<? super String>>> {

    private T1 a;
    private T2 b;

    public T1 getA() {
        return a;
    }

    public T2 getB() {
        return b;
    }

    @ViewOf(includePattern = ".*")
    public static class StaticChildBean<T1 extends String> {
        private T1 a;

        public T1 getA() {
            return a;
        }
    }

    @ViewOf(includePattern = ".*")
    public class DynamicChildBean<T3> {
        private T1 a;
        private T2 b;
        private T3 c;

        public T1 getA() {
            return a;
        }

        public T2 getB() {
            return b;
        }

        public T3 getC() {
            return c;
        }
    }
}
