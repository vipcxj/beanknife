package io.github.vipcxj.beanknife.jpa.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.List;

@Entity
@Getter
@Setter
public class Department {
    @Id
    private String number;
    @ManyToOne
    private Company company;
    @OneToMany(mappedBy = "department")
    private List<Employee> employees;
}
