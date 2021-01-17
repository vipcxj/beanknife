package io.github.vipcxj.beanknife.jpa.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
public class Company {
    @Id
    private String code;
    private String name;
    private double money;
    private Address address;
    @OneToMany(mappedBy = "company")
    private List<Department> departments;
    @OneToMany(mappedBy = "company")
    private List<Employee> employees;
}
