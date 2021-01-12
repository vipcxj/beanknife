package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.MethodAnnotation1;
import io.github.vipcxj.beanknife.cases.annotations.PropertyAnnotation1;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter(onMethod_ = {@PropertyAnnotation1})
public class LombokBean {
    private String a;
    @Getter(onMethod_ = {@MethodAnnotation1})
    private int b;
    private List<? extends Class<? extends Data>> c;
}
