package io.github.vipcxj.beanknfie.jpa.examples.dto;

import io.github.vipcxj.beanknfie.jpa.examples.models.Employee;
import io.github.vipcxj.beanknife.runtime.annotations.RemoveViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(Employee.class)
@RemoveViewProperty("company")
@RemoveViewProperty("department")
public class EmployeeInfoConfiguration extends BaseDtoConfiguration {
}
