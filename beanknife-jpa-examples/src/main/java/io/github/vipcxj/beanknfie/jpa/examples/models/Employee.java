package io.github.vipcxj.beanknfie.jpa.examples.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    private String number;
    private String name;
    @ManyToOne
    private Department department;
    @ManyToOne
    private Company company;
}
