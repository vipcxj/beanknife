package io.github.vipcxj.beanknife.tests.beans;

import io.github.vipcxj.beanknife.annotations.internal.GeneratedView;

import java.util.List;
import java.util.Set;

@GeneratedView(targetClass = TestBean3.class, configClass = TestBean3ViewConfig.class)
public class TestBean3View {

    private int a;
    private long b;
    private boolean c;
    private List<TestBean3> beans1;
    private Set<TestBean3> beans2;
    private long d2;
    private long e;

    public TestBean3View(
            int a,
            long b,
            boolean c,
            List<TestBean3> beans1,
            Set<TestBean3> beans2,
            long d2,
            long e
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.beans1 = beans1;
        this.beans2 = beans2;
        this.d2 = d2;
        this.e = e;
    }

    public TestBean3View(TestBean3View source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
        this.beans1 = source.beans1;
        this.beans2 = source.beans2;
        this.d2 = source.d2;
        this.e = source.e;
    }

    public static TestBean3View read(TestBean3 source) {
        return new TestBean3View(
                source.getA(),
                source.b,
                source.isC(),
                source.getBeans1(),
                source.getBeans2(),
                source.getD2(),
                TestBean3ViewConfig.e(source)
        );
    }

    public int getA() {
        return this.a;
    }

    public long getB() {
        return this.b;
    }

    public boolean isC() {
        return this.c;
    }

    public List<TestBean3> getBeans1() {
        return this.beans1;
    }

    public Set<TestBean3> getBeans2() {
        return this.beans2;
    }

    public long getD2() {
        return this.d2;
    }

    public long getE() {
        return this.e;
    }

    void setC(boolean c) {
        this.c = c;
    }

}