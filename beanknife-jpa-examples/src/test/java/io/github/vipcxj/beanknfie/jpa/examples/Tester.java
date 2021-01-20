package io.github.vipcxj.beanknfie.jpa.examples;

import io.github.vipcxj.beanknife.core.spi.ViewCodeGenerator;
import io.github.vipcxj.beanknfie.jpa.examples.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.ServiceLoader;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JpaConfig.class, loader = AnnotationConfigContextLoader.class)
public class Tester {

    @Autowired
    private EntityManager em;

    @Test
    @Transactional
    public void test() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Company company = new Company("001", "", 10000.0, new Address("Shanhai", "SanQuan Road", "888"), new ArrayList<>(), new ArrayList<>());
        em.persist(company);
        Department department = new Department("001001", company, new ArrayList<>());
        company.getDepartments().add(department);
        em.persist(department);
        Employee employee = new Employee("001001001", "a", department, company);
        department.getEmployees().add(employee);
        company.getEmployees().add(employee);
        em.persist(company);
        employee = new Employee("001001002", "b", department, company);
        department.getEmployees().add(employee);
        company.getEmployees().add(employee);
        em.persist(company);
        employee = new Employee("001001003", "c", department, company);
        department.getEmployees().add(employee);
        company.getEmployees().add(employee);
        em.persist(company);

        department = new Department("001002", company, new ArrayList<>());
        company.getDepartments().add(department);
        em.persist(department);
        employee = new Employee("001002001", "d", department, company);
        department.getEmployees().add(employee);
        company.getEmployees().add(employee);
        em.persist(employee);
        employee = new Employee("001002002", "e", department, company);
        department.getEmployees().add(employee);
        company.getEmployees().add(employee);
        em.persist(employee);
        employee = new Employee("001002003", "f", department, company);
        department.getEmployees().add(employee);
        company.getEmployees().add(employee);
        em.persist(employee);

        Query query = em.createQuery("select c, d from Company c left join fetch c.departments d");
        for (Object o : query.getResultList()) {
            System.out.println(o);
        }
        ;
//        CriteriaQuery<EmployeeInfo> query = cb.createQuery(EmployeeInfo.class);
//        Root<Employee> employees = query.from(Employee.class);
//        Join<Employee, Company> company = employees.join(EmployeeMeta.company, JoinType.LEFT);
//        Join<Employee, Department> departments = employees.join(EmployeeMeta.department, JoinType.LEFT);
//        Selection<EmployeeInfo> selection = EmployeeInfo.toJpaSelection(
//                cb, employees,
//                DepartmentInfo.toJpaSelection(cb, departments, departments.get(DepartmentMeta.employees)),
//                CompanyInfo.toJpaSelection(cb, company)
//        );
//        query.select(selection);
//        em.createQuery(query).getResultList();

    }
}
