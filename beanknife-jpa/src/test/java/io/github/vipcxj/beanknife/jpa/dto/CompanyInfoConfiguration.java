package io.github.vipcxj.beanknife.jpa.dto;

import io.github.vipcxj.beanknife.jpa.models.Company;
import io.github.vipcxj.beanknife.jpa.models.CompanyMeta;
import io.github.vipcxj.beanknife.runtime.annotations.RemoveViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(Company.class)
@RemoveViewProperty({CompanyMeta.departments, CompanyMeta.employees})
public class CompanyInfoConfiguration extends BaseDtoConfiguration {
}
