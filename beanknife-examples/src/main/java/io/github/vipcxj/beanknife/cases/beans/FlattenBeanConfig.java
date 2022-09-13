package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.*;

@ViewOf(value = BeanB.class, genName = "FlattenBeanBView", includePattern = ".*")
public class FlattenBeanConfig {

    @OverrideViewProperty("beanA")
    @Flatten(nameMapper = "${name}Flatten", includes = {"a", "c", "beanBMap"})
    private BeanAView beanA;

    @OverrideViewProperty("anotherBeanA")
    private BeanAView anotherBeanA;

    @NewViewProperty("dynamicBeanA")
    @Dynamic
    @Flatten(nameMapper = "${name}DynFlatten", includes = {"c", "beanBMap", "d"})
    public BeanAView dynamicBeanA(@InjectProperty("anotherBeanA") BeanAView anotherBeanA) {
        return anotherBeanA;
    }
}
