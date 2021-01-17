package io.github.vipcxj.beanknife.jpa.dao;

import io.github.vipcxj.beanknife.jpa.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, String> {
}
