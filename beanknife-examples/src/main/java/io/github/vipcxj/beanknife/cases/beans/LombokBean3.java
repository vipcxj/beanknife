package io.github.vipcxj.beanknife.cases.beans;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LombokBean3 extends LombokBean {
    private boolean d;
}
