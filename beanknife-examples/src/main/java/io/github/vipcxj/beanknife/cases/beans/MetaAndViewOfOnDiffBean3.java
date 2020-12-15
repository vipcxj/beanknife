package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(includes = {MetaAndViewOfOnDiffBean3Meta.pa, MetaAndViewOfOnDiffBean3Meta.pb})
public class MetaAndViewOfOnDiffBean3 {

    private String pa;
    private int pb;

    public String getPa() {
        return pa;
    }

    public int getPb() {
        return pb;
    }
}
