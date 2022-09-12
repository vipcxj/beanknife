package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.Flatten;
import io.github.vipcxj.beanknife.runtime.annotations.OverrideViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = BeanB.class, genName = "FlattenBeanBView", includePattern = ".*")
public class FlattenBeanConfig {

    @OverrideViewProperty("beanA")
    @Flatten(nameMapper = "${name}Flatten", includes = {"a", "c", "beanBMap"})
    private BeanAView beanA;
}
