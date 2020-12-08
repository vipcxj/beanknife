package io.github.vipcxj.beanknife.cases.meta;

import io.github.vipcxj.beanknife.annotations.ViewMeta;
import io.github.vipcxj.beanknife.cases.models.AObject;

import java.util.Date;

@ViewMeta
public class MixedAllBean {

    protected int a;
    int b;
    private long c;
    private short d;
    private AObject f;
    private String _if;
    private Date _while;
    boolean isIsProperty;
    Boolean isObjectIsProperty;
    public String hideProperty;
    private int TV;
    String ABC;
    private long iI;
    Date aBC;

    public short getD() {
        return d;
    }

    public AObject getF() {
        return f;
    }

    protected boolean isMe() {
        return true;
    }

    public boolean isIsProperty() {
        return isIsProperty;
    }

    public boolean isObjectIsProperty() {
        return isObjectIsProperty;
    }

    private String getHideProperty() {
        return hideProperty;
    }

    public int getTV() {
        return TV;
    }

    public long getiI() {
        return iI;
    }
}
