package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.*;
import io.github.vipcxj.beanknife.cases.models.AEnum;

import java.util.Date;
import java.util.List;

@TypeAnnotation(Date.class)
@DocumentedTypeAnnotation(enumValue = AEnum.B, enumValues = {AEnum.C, AEnum.B, AEnum.A})
public class AnnotationBean extends BaseAnnotationBean {

    @FieldAnnotation1(doubleArray = 1.0)
    private String a;
    @FieldAnnotation2(annotation = @ValueAnnotation1(type = Date.class))
    private String b;
    @FieldAnnotation1(charValue = '0')
    @FieldAnnotation2(stringArray = "5")
    private String[] c;
    @FieldAnnotation1(annotations = {
            @ValueAnnotation1(),
            @ValueAnnotation1(type = AnnotationBean.class)
    })
    @PropertyAnnotation1
    private int d;
    @PropertyAnnotation2(stringValue = "field_e")
    private Date e;
    @PropertyAnnotation2(stringValue = "field_f")
    private List<String> f;
    @PropertyAnnotation1(stringValue = "field_g")
    private short g;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String[] getC() {
        return c;
    }

    public void setC(String[] c) {
        this.c = c;
    }

    @MethodAnnotation1(
            charValue = 'a',
            annotations = {
                    @ValueAnnotation1,
                    @ValueAnnotation1(
                            annotations = @ValueAnnotation2
                    )
            }
    )
    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    @PropertyAnnotation2(stringValue = "getter_e")
    public Date getE() {
        return e;
    }

    @PropertyAnnotation2(stringValue = "getter_f")
    public List<String> getF() {
        return f;
    }

    @PropertyAnnotation1(stringValue = "getter_g")
    public short getG() {
        return g;
    }
}
