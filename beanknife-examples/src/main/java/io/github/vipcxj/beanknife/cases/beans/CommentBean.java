package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.annotations.ViewOf;

@ViewOf(includePattern = ".*")
public class CommentBean {

    /**
     * this is a.
     * this is the second line.
     * {@link Object} this is some doc annotation.
     */
    private String a;
    private String b;
    /**
     * this is c
     * this comment is on the field.
     * this is the second line.
     * {@link Object} this is some doc annotation.
     */
    private long c;

    public String getA() {
        return a;
    }

    /**
     * this is b.
     * this is the second line.
     * {@link Object} this is some doc annotation.
     */
    public String getB() {
        return b;
    }

    /**
     * this is c
     * this comment is on the method.
     * this is the second line.
     * {@link Object} this is some doc annotation.
     */
    public long getC() {
        return c;
    }
}
