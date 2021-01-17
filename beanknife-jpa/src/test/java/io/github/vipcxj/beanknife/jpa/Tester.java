package io.github.vipcxj.beanknife.jpa;

import io.github.vipcxj.beanknife.jpa.models.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = JpaConfig.class, loader = AnnotationConfigContextLoader.class)
public class Tester {

    @Autowired
    private EntityManager em;

    @Test
    public void test() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DepartmentInfo> query = cb.createQuery(DepartmentInfo.class);
        Root<Department> departments = query.from(Department.class);
        Join<Department, Employee> employees = departments.join(DepartmentMeta.employees, JoinType.LEFT);
        CompoundSelection<DepartmentInfo> selection = cb.construct(
                DepartmentInfo.class,
                departments.get(DepartmentMeta.number),
                cb.construct(
                        CompanyInfo.class,
                        employees.get(EmployeeMeta.number),
                        employees.get(EmployeeMeta.name),
                        cb.nullLiteral(DepartmentInfo.class),
                        cb.nullLiteral(CompanyInfo.class)
                )
        );
        query.select(selection);
        em.createQuery(query).getResultList();
    }
}
