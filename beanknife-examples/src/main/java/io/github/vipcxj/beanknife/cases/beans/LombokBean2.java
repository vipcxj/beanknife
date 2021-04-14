package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.annotations.MethodAnnotation1;
import io.github.vipcxj.beanknife.cases.annotations.PropertyAnnotation1;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
public class LombokBean2 {
    private String a;
    @Getter(value = AccessLevel.PROTECTED, onMethod_ = {@MethodAnnotation1})
    private int b;
    private List<? extends Class<? extends Data>> c;
    final private long d;
}
