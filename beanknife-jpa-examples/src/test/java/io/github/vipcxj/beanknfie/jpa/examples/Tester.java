package io.github.vipcxj.beanknfie.jpa.examples;

import io.github.vipcxj.beanknfie.jpa.examples.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JpaConfig.class, loader = AnnotationConfigContextLoader.class)
public class Tester {

    @Autowired
    private EntityManager em;

    private final Random random = new Random();

    private long random() {
        long unit = 3000 * 24 * 3600;
        return (long) (random.nextDouble() * unit) - unit / 2;
    }

    private Date randomDate(Instant instant) {
        return Date.from(instant.plusSeconds(random()));
    }

    @Test
    @Transactional
    public void test() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        Company company = new Company("001", "google", 10000.0, new Address("Shanhai", "SanQuan Road", "888"), new ArrayList<>(), new ArrayList<>());
        em.persist(company);
        Department department = new Department("001001", company, new ArrayList<>());
        company.getDepartments().add(department);
        em.persist(department);
        Instant baseBirthDay = Instant.parse("1988-03-24T00:00:00.00Z");
        Instant baseEnrollmentDay = Instant.parse("1999-01-03T00:00:00.00Z");
        Employee employee = new Employee("001001001", "a", "male", "", randomDate(baseBirthDay), randomDate(baseEnrollmentDay), department, company);
        department.getEmployees().add(employee);
        company.getEmployees().add(employee);
        em.persist(employee);
        employee = new Employee("001001002", "b", "male", "", randomDate(baseBirthDay), randomDate(baseEnrollmentDay), department, company);
        department.getEmployees().add(employee);
        company.getEmployees().add(employee);
        em.persist(employee);
        employee = new Employee("001001003", "c", "male", "", randomDate(baseBirthDay), randomDate(baseEnrollmentDay), department, company);
        department.getEmployees().add(employee);
        company.getEmployees().add(employee);
        em.persist(employee);

        department = new Department("001002", company, new ArrayList<>());
        company.getDepartments().add(department);
        em.persist(department);
        employee = new Employee("001002001", "d", "male", "", randomDate(baseBirthDay), randomDate(baseEnrollmentDay), department, company);
        department.getEmployees().add(employee);
        company.getEmployees().add(employee);
        em.persist(employee);
        employee = new Employee("001002002", "e", "male", "", randomDate(baseBirthDay), randomDate(baseEnrollmentDay), department, company);
        department.getEmployees().add(employee);
        company.getEmployees().add(employee);
        em.persist(employee);
        employee = new Employee("001002003", "f", "male", "", randomDate(baseBirthDay), randomDate(baseEnrollmentDay), department, company);
        department.getEmployees().add(employee);
        company.getEmployees().add(employee);
        em.persist(employee);

        CriteriaQuery<EmployeeDetail> query = cb.createQuery(EmployeeDetail.class);
        Root<Employee> employees = query.from(Employee.class);
        Selection<EmployeeDetail> employeeInfoSelection = EmployeeDetail.toJpaSelection(cb, employees);
        List<EmployeeDetail> resultList = em.createQuery(query.select(employeeInfoSelection)).getResultList();
        for (EmployeeDetail detail : resultList) {
            System.out.print("{");
            System.out.print("number: ");
            System.out.print(detail.getNumber());
            System.out.print(", name: ");
            System.out.print(detail.getName());
            System.out.print(", sex: ");
            System.out.print(detail.getSex());
            System.out.print(", nation: ");
            System.out.print(detail.getNation());
            System.out.print(", companyCode:");
            System.out.print(detail.getCompanyInfo().getCode());
            System.out.print(", companyName:");
            System.out.print(detail.getCompanyInfo().getName());
            System.out.print(", companyMoney:");
            System.out.print(detail.getCompanyInfo().getMoney());
            System.out.print(", companyAddress:");
            System.out.print(detail.getCompanyInfo().getAddress().toString());
            System.out.println("}");
        }


    }
}
