package io.github.vipcxj.beanknife.tests.beans;

import io.github.vipcxj.beanknife.runtime.annotations.Access;
import io.github.vipcxj.beanknife.runtime.annotations.NewViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.RemoveViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = TestBean3.class, emptyConstructor = Access.NONE, includePattern = ".*", excludes = {"d1"})
@RemoveViewProperty("beans1")
@RemoveViewProperty("beans2")
public class TestBean3ViewConfig {

    @NewViewProperty("e")
    static long e(TestBean3 source) {
        return source.getA() + source.b;
    }
}