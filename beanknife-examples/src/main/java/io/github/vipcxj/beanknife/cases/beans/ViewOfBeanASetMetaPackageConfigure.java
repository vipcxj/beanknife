package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewMetaPackage;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = BeanA.class, genName = "BeanAView1SetMetaPackage")
@ViewOf(value = BeanA.class, genName = "BeanAView2SetMetaPackage")
@ViewMetaPackage("io.github.vipcxj.beanknife.cases.otherbean")
public class ViewOfBeanASetMetaPackageConfigure {
}
