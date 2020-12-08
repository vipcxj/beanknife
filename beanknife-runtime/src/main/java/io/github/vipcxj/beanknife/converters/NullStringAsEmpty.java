package io.github.vipcxj.beanknife.converters;

import io.github.vipcxj.beanknife.annotations.UsePropertyConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.SOURCE)
@UsePropertyConverter(NullStringAsEmptyConverter.class)
public @interface NullStringAsEmpty {
}
