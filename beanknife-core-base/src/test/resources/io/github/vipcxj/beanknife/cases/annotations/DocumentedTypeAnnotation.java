package io.github.vipcxj.beanknife.cases.annotations;

import io.github.vipcxj.beanknife.cases.models.AEnum;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DocumentedTypeAnnotation {
    AEnum enumValue() default AEnum.A;
    AEnum[] enumValues() default {};
}
