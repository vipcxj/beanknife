package io.github.vipcxj.beanknife.jpa.dto;

import io.github.vipcxj.beanknife.jpa.models.CompanyInfo;
import io.github.vipcxj.beanknife.jpa.models.DepartmentInfo;
import io.github.vipcxj.beanknife.jpa.models.Employee;
import io.github.vipcxj.beanknife.jpa.runtime.annotations.AddJpaSupport;
import io.github.vipcxj.beanknife.runtime.annotations.MapViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(Employee.class)
@AddJpaSupport
public class EmployeeInfoConfiguration extends BaseDtoConfiguration {

    @MapViewProperty(name = "departmentInfo", map = "department")
    private DepartmentInfo departmentInfo;

    @MapViewProperty(name = "companyInfo", map = "company")
    private CompanyInfo companyInfo;
}
