package io.github.vipcxj.beanknife.runtime.annotations;

import io.github.vipcxj.beanknife.runtime.converters.*;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
@UsePropertyConverter(NullBigDecimalAsZeroConverter.class)
@UsePropertyConverter(NullBigIntegerAsZeroConverter.class)
@UsePropertyConverter(NullByteAsZeroConverter.class)
@UsePropertyConverter(NullDoubleAsZeroConverter.class)
@UsePropertyConverter(NullFloatAsZeroConverter.class)
@UsePropertyConverter(NullIntegerAsZeroConverter.class)
@UsePropertyConverter(NullLongAsZeroConverter.class)
@UsePropertyConverter(NullShortAsZeroConverter.class)
public @interface NullNumberAsZero { }
