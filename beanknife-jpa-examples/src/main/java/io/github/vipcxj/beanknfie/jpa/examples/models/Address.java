package io.github.vipcxj.beanknfie.jpa.examples.models;

import lombok.*;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Address {

    private String city;
    private String road;
    private String number;
}
