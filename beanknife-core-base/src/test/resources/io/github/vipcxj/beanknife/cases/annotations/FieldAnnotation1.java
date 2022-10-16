package io.github.vipcxj.beanknife.cases.annotations;

import io.github.vipcxj.beanknife.cases.models.AEnum;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldAnnotation1 {
    boolean booleanValue() default true;
    int intValue() default 0;
    short shortValue() default 0;
    long longValue() default 0;
    float floatValue() default 0.0f;
    double doubleValue() default 0.0;
    char charValue() default '\0';
    byte byteValue() default 0;
    int[] intArray() default {0};
    short[] shortArray() default {};
    long[] longArray() default {0};
    float[] floatArray() default {};
    double[] doubleArray() default {0.0};
    char[] charArray() default {};
    byte[] byteArray() default {};
    String stringValue() default "";
    String[] stringArray() default {"1", "2", "3"};
    Class<? extends Annotation> annotationClass() default FieldAnnotation1.class;
    Class<? extends Enum<?>>[] enumClassArray() default AEnum.class;
    ValueAnnotation1 annotation() default @ValueAnnotation1;
    ValueAnnotation1[] annotations() default @ValueAnnotation1;
}
