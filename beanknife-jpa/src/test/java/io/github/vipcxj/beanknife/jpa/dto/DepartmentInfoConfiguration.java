package io.github.vipcxj.beanknife.jpa.dto;

import io.github.vipcxj.beanknife.jpa.models.*;
import io.github.vipcxj.beanknife.runtime.annotations.MapViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.OverrideViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

import java.util.List;

@ViewOf(Department.class)
public class DepartmentInfoConfiguration extends BaseDtoConfiguration {

    @MapViewProperty(name = "companyInfo", map = DepartmentMeta.company)
    private CompanyInfo companyInfo;
    @OverrideViewProperty(DepartmentMeta.employees)
    private List<EmployeeInfo> employees;
}
