package io.github.vipcxj.beanknfie.jpa.examples.dto;

import io.github.vipcxj.beanknfie.jpa.examples.models.Employee;
import io.github.vipcxj.beanknfie.jpa.examples.models.EmployeeMeta;
import io.github.vipcxj.beanknife.runtime.annotations.RemoveViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(Employee.class)
@RemoveViewProperty({EmployeeMeta.company, EmployeeMeta.department})
public class EmployeeInfoConfiguration extends BaseDtoConfiguration {
}
