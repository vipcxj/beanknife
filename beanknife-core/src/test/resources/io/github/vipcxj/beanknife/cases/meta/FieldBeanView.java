package io.github.vipcxj.beanknife.cases.meta;

import java.util.Date;

public class FieldBeanView {

    private long b;

    private Date c;

    public FieldBeanView() { }

    public FieldBeanView(
            long b,
            Date c
    ) {
        this.b = b;
        this.c = c;
    }

    public FieldBeanView(FieldBeanView source) {
        this.b = source.b;
        this.c = source.c;
    }

    public static FieldBeanView read(FieldBean source) {
        FieldBeanView out = new FieldBeanView();
        out.b = source.b;
        out.c = source.c;
        return out;
    }

    public long getB() {
        return this.b;
    }

    public Date getC() {
        return this.c;
    }

}
