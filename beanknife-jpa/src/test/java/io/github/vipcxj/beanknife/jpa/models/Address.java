package io.github.vipcxj.beanknife.jpa.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
@Setter
public class Address {

    private String city;
    private String road;
    private String number;
}
