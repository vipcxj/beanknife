package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.DocumentedTypeAnnotation;
import io.github.vipcxj.beanknife.cases.annotations.FieldAnnotation1;
import io.github.vipcxj.beanknife.cases.annotations.FieldAnnotation2;
import io.github.vipcxj.beanknife.cases.annotations.InheritableTypeAnnotation;
import io.github.vipcxj.beanknife.cases.annotations.MethodAnnotation1;
import io.github.vipcxj.beanknife.cases.annotations.PropertyAnnotation1;
import io.github.vipcxj.beanknife.cases.annotations.PropertyAnnotation2;
import io.github.vipcxj.beanknife.cases.annotations.TypeAnnotation;
import io.github.vipcxj.beanknife.cases.annotations.ValueAnnotation1;
import io.github.vipcxj.beanknife.cases.annotations.ValueAnnotation2;
import io.github.vipcxj.beanknife.cases.models.AEnum;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = AnnotationBean.class, configClass = AnnotationBeanViewConfigure.class)
@InheritableTypeAnnotation(
    annotation = @ValueAnnotation1(type = {
        int.class
    }),
    annotations = {
        @ValueAnnotation1(type = {
            void.class
        }),
        @ValueAnnotation1(type = {
            String.class
        }),
        @ValueAnnotation1(type = {
            int[][][].class
        }),
        @ValueAnnotation1(type = {
            Void.class
        }),
        @ValueAnnotation1(type = {
            Void[].class
        })
    }
)
@TypeAnnotation(Date.class)
@DocumentedTypeAnnotation(
    enumValue = AEnum.B,
    enumValues = {
        AEnum.C,
        AEnum.B,
        AEnum.A
    }
)
public class AnnotationBeanView {

    @FieldAnnotation1(enumClassArray = {
        AEnum.class,
        AEnum.class
    })
    private Class<?> type;

    @FieldAnnotation1(doubleArray = {
        1.0
    })
    private String a;

    private String b;

    @FieldAnnotation1(charValue = '0')
    @FieldAnnotation2(stringArray = {
        "5"
    })
    private String[] c;

    private int d;

    @PropertyAnnotation2(stringValue = "config_e")
    private Date e;

    @PropertyAnnotation2(stringValue = "field_f")
    private List<String> f;

    @PropertyAnnotation1(stringValue = "field_g")
    private short g;

    public AnnotationBeanView() { }

    public AnnotationBeanView(
        Class<?> type,
        String a,
        String b,
        String[] c,
        int d,
        Date e,
        List<String> f,
        short g
    ) {
        this.type = type;
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
        this.f = f;
        this.g = g;
    }

    public AnnotationBeanView(AnnotationBeanView source) {
        this.type = source.type;
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
        this.d = source.d;
        this.e = source.e;
        this.f = source.f;
        this.g = source.g;
    }

    public AnnotationBeanView(AnnotationBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.AnnotationBeanView should not be null.");
        }
        this.type = source.type;
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
        this.d = source.getD();
        this.e = source.getE();
        this.f = source.getF();
        this.g = source.getG();
    }

    public static AnnotationBeanView read(AnnotationBean source) {
        if (source == null) {
            return null;
        }
        return new AnnotationBeanView(source);
    }

    public static AnnotationBeanView[] read(AnnotationBean[] sources) {
        if (sources == null) {
            return null;
        }
        AnnotationBeanView[] results = new AnnotationBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<AnnotationBeanView> read(List<AnnotationBean> sources) {
        if (sources == null) {
            return null;
        }
        List<AnnotationBeanView> results = new ArrayList<>();
        for (AnnotationBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<AnnotationBeanView> read(Set<AnnotationBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<AnnotationBeanView> results = new HashSet<>();
        for (AnnotationBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<AnnotationBeanView> read(Stack<AnnotationBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<AnnotationBeanView> results = new Stack<>();
        for (AnnotationBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, AnnotationBeanView> read(Map<K, AnnotationBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, AnnotationBeanView> results = new HashMap<>();
        for (Map.Entry<K, AnnotationBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public Class<?> getType() {
        return this.type;
    }

    public String getA() {
        return this.a;
    }

    public String getB() {
        return this.b;
    }

    public String[] getC() {
        return this.c;
    }

    @PropertyAnnotation1
    @MethodAnnotation1(
        charValue = 'a',
        annotations = {
            @ValueAnnotation1,
            @ValueAnnotation1(annotations = {
                @ValueAnnotation2
            })
        }
    )
    public int getD() {
        return this.d;
    }

    public Date getE() {
        return this.e;
    }

    public List<String> getF() {
        return this.f;
    }

    @PropertyAnnotation1(stringValue = "getter_g")
    public short getG() {
        return this.g;
    }

}
