package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewMeta;

import java.util.Date;

@ViewMeta
public class MethodBean {

    protected Date getNow() {
        return new Date();
    }
}
