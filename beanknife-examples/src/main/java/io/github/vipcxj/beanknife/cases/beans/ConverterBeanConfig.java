package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.annotations.NullNumberAsZero;
import io.github.vipcxj.beanknife.annotations.OverrideViewProperty;
import io.github.vipcxj.beanknife.annotations.ViewMeta;
import io.github.vipcxj.beanknife.annotations.ViewOf;

@ViewMeta(of = ConverterBean.class)
@ViewOf(value = ConverterBean.class, includes = {ConverterBeanMeta.a, ConverterBeanMeta.b})
public class ConverterBeanConfig {

    @OverrideViewProperty(ConverterBeanMeta.a)
    @NullNumberAsZero
    private long a;

    @OverrideViewProperty(ConverterBeanMeta.b)
    @NullNumberAsZero
    private Number b;
}
