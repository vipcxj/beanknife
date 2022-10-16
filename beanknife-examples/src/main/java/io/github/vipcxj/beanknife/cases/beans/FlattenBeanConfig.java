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

    @NewViewProperty("innerStaticBean")
    @Flatten(nameMapper = "innerStaticBean${Name}", includePattern = ".*")
    public TestStaticBean innerStaticBean() {
        return new TestStaticBean();
    }

    public static class TestStaticBean {
        public int a;
        private String b;
        private char c;

        public String getB() {
            return b;
        }
    }
}
