package io.github.vipcxj.beanknife.tests.beans;

public class TestBean3View {
    private int a;
    private long b;
    private boolean c;
    private long d2;
    private long e;

    public TestBean3View() {}

    public TestBean3View(
        int a,
        long b,
        boolean c,
        long d2,
        long e
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d2 = d2;
        this.e = e;
    }

    public TestBean3View(TestBean3 source) {
        this.a = source.getA();
        this.b = source.b;
        this.c = source.isC();
        this.d2 = source.getD2();
        this.e = TestBean3ViewConfig.e(source);
    }

    public static TestBean3View read(TestBean3 source) {
        return new TestBean3View(
                source.getA(),
                source.b,
                source.isC(),
                source.getD2(),
                TestBean3ViewConfig.e(source)
        );
    }

    public int getA() {
        return a;
    }

    public long getB() {
        return b;
    }

    public boolean isC() {
        return c;
    }

    void setC(boolean c) {
        this.c = c;
    }

    public long getD2() {
        return d2;
    }

    public long getE() {
        return e;
    }
}