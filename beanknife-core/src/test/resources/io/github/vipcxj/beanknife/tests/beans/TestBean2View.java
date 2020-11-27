package io.github.vipcxj.beanknife.tests.beans;

public class TestBean2View {

    private final int a;
    private final boolean c;
    private final long d2;

    public TestBean2View(int a, boolean c, long d2) {
        this.a = a;
        this.c = c;
        this.d2 = d2;
    }

    public TestBean2View(TestBean2 source) {
        this(source.getA(), source.getC(), source.getD2());
    }

    public static TestBean2View from(TestBean2 source) {
        return new TestBean2View(source);
    }

    public int getA() {
        return a;
    }

    public boolean isC() {
        return c;
    }

    protected long getD2() {
        return d2;
    }

}
