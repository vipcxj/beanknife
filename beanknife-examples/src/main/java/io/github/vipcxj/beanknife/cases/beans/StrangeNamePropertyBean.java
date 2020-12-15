package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.ViewMeta;

import java.util.Date;

@ViewMeta
public class StrangeNamePropertyBean {

    private int TV;
    String ABC;
    private long iI;
    Date aBC;
    private boolean _boolean;
    short _Short;

    public int getTV() {
        return TV;
    }

    public long getiI() {
        return iI;
    }

    public boolean is_boolean() {
        return _boolean;
    }
}
