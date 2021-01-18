package io.github.vipcxj.beanknfie.jpa.examples.dto;

import io.github.vipcxj.beanknfie.jpa.examples.models.Department;
import io.github.vipcxj.beanknfie.jpa.examples.models.DepartmentMeta;
import io.github.vipcxj.beanknfie.jpa.examples.models.EmployeeInfo;
import io.github.vipcxj.beanknife.runtime.annotations.OverrideViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.RemoveViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

import java.util.List;

@ViewOf(Department.class)
@RemoveViewProperty(DepartmentMeta.company)
public class DepartmentInfoConfiguration extends BaseDtoConfiguration {

    @OverrideViewProperty(DepartmentMeta.employees)
    private List<EmployeeInfo> employees;
}
