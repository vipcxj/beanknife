package io.github.vipcxj.beanknife.jpa.dao;

import io.github.vipcxj.beanknife.jpa.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
}
