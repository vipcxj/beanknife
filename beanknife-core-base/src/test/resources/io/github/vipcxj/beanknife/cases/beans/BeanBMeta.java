package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedMeta;

@GeneratedMeta(
    targetClass = BeanB.class,
    configClass = BeanB.class,
    proxies = {
        ConfigBeanB.class,
        InheritedConfigBeanBViewConfig.class
    }
)
public class BeanBMeta {
    public static final String a = "a";
    public static final String beanA = "beanA";

    public static class Views {
        public static final String io_github_vipcxj_beanknife_cases_beans_BeanBView = "io.github.vipcxj.beanknife.cases.beans.BeanBView";
        public static final String io_github_vipcxj_beanknife_cases_beans_BeanBViewWithInheritedConfig = "io.github.vipcxj.beanknife.cases.beans.BeanBViewWithInheritedConfig";
    }
}
