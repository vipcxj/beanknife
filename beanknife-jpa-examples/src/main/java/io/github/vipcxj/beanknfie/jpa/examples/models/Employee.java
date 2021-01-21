package io.github.vipcxj.beanknfie.jpa.examples.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {
    @Id
    private String number;
    private String name;
    private String sex;
    private String nation;
    @Temporal(TemporalType.DATE)
    private Date birthDay;
    @Temporal(TemporalType.DATE)
    private Date enrollmentDay;
    @ManyToOne
    private Department department;
    @ManyToOne
    private Company company;
}
