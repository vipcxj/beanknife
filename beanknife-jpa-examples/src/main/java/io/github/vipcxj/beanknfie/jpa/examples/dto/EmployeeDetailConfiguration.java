package io.github.vipcxj.beanknfie.jpa.examples.dto;

import io.github.vipcxj.beanknfie.jpa.examples.models.CompanyInfo;
import io.github.vipcxj.beanknfie.jpa.examples.models.DepartmentInfo;
import io.github.vipcxj.beanknfie.jpa.examples.models.Employee;
import io.github.vipcxj.beanknife.runtime.annotations.MapViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(value = Employee.class, genName = "EmployeeDetail")
public class EmployeeDetailConfiguration extends BaseDtoConfiguration {

    @MapViewProperty(name = "departmentInfo", map = "department")
    private DepartmentInfo departmentInfo;

    @MapViewProperty(name = "companyInfo", map = "company")
    private CompanyInfo companyInfo;
}
