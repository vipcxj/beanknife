package io.github.vipcxj.beanknife.jpa.dao;

import io.github.vipcxj.beanknife.jpa.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, String> {
}
