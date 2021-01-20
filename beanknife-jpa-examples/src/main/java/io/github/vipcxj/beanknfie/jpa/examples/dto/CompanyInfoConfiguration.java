package io.github.vipcxj.beanknfie.jpa.examples.dto;

import io.github.vipcxj.beanknfie.jpa.examples.models.Company;
import io.github.vipcxj.beanknfie.jpa.examples.models.CompanyMeta;
import io.github.vipcxj.beanknife.runtime.annotations.NullNumberAsZero;
import io.github.vipcxj.beanknife.runtime.annotations.OverrideViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.RemoveViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(Company.class)
@RemoveViewProperty("departments")
@RemoveViewProperty("employees")
public class CompanyInfoConfiguration extends BaseDtoConfiguration {

    @OverrideViewProperty(CompanyMeta.money)
    @NullNumberAsZero
    private double money;
}
