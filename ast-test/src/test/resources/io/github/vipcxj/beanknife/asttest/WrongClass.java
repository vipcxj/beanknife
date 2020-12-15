package io.github.vipcxj.beanknife.asttest;

import java.util.Date;
import java.util.Set;

@TestAnnotation
public class WrongClass implements AInterface<Number, String> {

    public Set<? extends Date> test() {
        return null;
    }
}