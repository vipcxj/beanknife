package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

public class MetaAndViewOfOnDiffBean4 {

    private String pa;
    private int pb;

    public String getPa() {
        return pa;
    }

    public int getPb() {
        return pb;
    }

    @ViewOf(includePattern = ".*", excludes = {MetaAndViewOfOnDiffBean4$NestedBeanMeta.paOfParent})
    class NestedBean {

        private final String paOfParent;
        private final String pa;
        private int pb;

        public NestedBean(String pa, int pb) {
            this.pa = pa;
            this.pb = pb;
            this.paOfParent = MetaAndViewOfOnDiffBean4.this.pa;
        }

        public String getPaOfParent() {
            return paOfParent;
        }

        public String getPa() {
            return pa;
        }

        public int getPb() {
            return pb;
        }
    }
}
