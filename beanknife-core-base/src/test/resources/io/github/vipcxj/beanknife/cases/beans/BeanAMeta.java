package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedMeta;

@GeneratedMeta(
    targetClass = BeanA.class,
    configClass = BeanA.class,
    proxies = {
        ConfigBeanA.class,
        InheritedConfigBeanAViewConfig.class
    }
)
public class BeanAMeta {
    public static final String a = "a";
    public static final String b = "b";
    public static final String c = "c";
    public static final String beanBMap = "beanBMap";

    public static class Views {
        public static final String io_github_vipcxj_beanknife_cases_beans_BeanAView = "io.github.vipcxj.beanknife.cases.beans.BeanAView";
        public static final String io_github_vipcxj_beanknife_cases_beans_BeanAViewWithInheritedConfig = "io.github.vipcxj.beanknife.cases.beans.BeanAViewWithInheritedConfig";
    }
}
