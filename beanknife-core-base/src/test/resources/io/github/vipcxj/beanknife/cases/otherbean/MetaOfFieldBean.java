package io.github.vipcxj.beanknife.cases.otherbean;

import io.github.vipcxj.beanknife.cases.beans.ChangePackageAndNameMetaConfig;
import io.github.vipcxj.beanknife.cases.beans.FieldBean;
import io.github.vipcxj.beanknife.cases.beans.FieldBeanViewConfig;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedMeta;

@GeneratedMeta(
    targetClass = FieldBean.class,
    configClass = ChangePackageAndNameMetaConfig.class,
    proxies = {
        FieldBeanViewConfig.class
    }
)
public class MetaOfFieldBean {
    public static final String b = "b";
    public static final String c = "c";
    public static final String d = "d";

    public static class Views {
        public static final String io_github_vipcxj_beanknife_cases_beans_FieldBeanView = "io.github.vipcxj.beanknife.cases.beans.FieldBeanView";
    }
}
