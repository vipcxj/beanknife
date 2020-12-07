package io.github.vipcxj.beanknife.tests.beans;

import io.github.vipcxj.beanknife.annotations.*;

import java.util.Date;

@ViewOf(value = TestBean3.class, includePattern = ".*", excludes = {"d1"})
@RemoveViewProperty("beans1")
@RemoveViewProperty("beans2")
public class TestBean3ViewConfig {

    @NewViewProperty("e")
    static long e(TestBean3 source) {
        return source.getA() + source.b;
    }
}