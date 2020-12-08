package io.github.vipcxj.beanknife.cases.meta;

import io.github.vipcxj.beanknife.annotations.ViewMeta;

import java.util.Date;

@ViewMeta
public class MethodBean {

    protected Date getNow() {
        return new Date();
    }
}
