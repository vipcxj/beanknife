package io.github.vipcxj.beanknfie.jpa.examples.dao;

import io.github.vipcxj.beanknfie.jpa.examples.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, String> {
}
