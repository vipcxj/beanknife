package io.github.vipcxj.beanknfie.jpa.examples.dao;

import io.github.vipcxj.beanknfie.jpa.examples.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, String> {
}
