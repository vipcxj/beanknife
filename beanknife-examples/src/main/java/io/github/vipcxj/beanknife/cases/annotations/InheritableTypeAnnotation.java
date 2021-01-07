package io.github.vipcxj.beanknife.cases.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface InheritableTypeAnnotation {
    ValueAnnotation1 annotation();
    ValueAnnotation1[] annotations();
}
