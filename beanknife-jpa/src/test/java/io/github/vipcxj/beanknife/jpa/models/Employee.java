package io.github.vipcxj.beanknife.jpa.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class Employee {
    @Id
    private String number;
    private String name;
    @ManyToOne
    private Department department;
    @ManyToOne
    private Company company;
}
