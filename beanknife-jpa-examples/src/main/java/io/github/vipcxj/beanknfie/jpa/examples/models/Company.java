package io.github.vipcxj.beanknfie.jpa.examples.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
