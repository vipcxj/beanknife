package io.github.vipcxj.beanknfie.jpa.examples.dao;

import io.github.vipcxj.beanknfie.jpa.examples.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
}
