package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.cases.models.AObject;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = InheritedBean.class, configClass = InheritedBeanConfigure.class)
public class InheritedBeanView {

    private int b;

    private boolean isProperty;

    private Boolean isObjectIsProperty;

    private String hideProperty;

    private String ABC;

    private Date aBC;

    private short _Short;

    private short d;

    private AObject f;

    private boolean me;

    private Date while_;

    private boolean objectIsProperty;

    private int TV;

    private long iI;

    private boolean _boolean;

    private String newProperty;

    private String if_;

    public InheritedBeanView() { }

    public InheritedBeanView(
        int b,
        boolean isProperty,
        Boolean isObjectIsProperty,
        String hideProperty,
        String ABC,
        Date aBC,
        short _Short,
        short d,
        AObject f,
        boolean me,
        Date while_,
        boolean objectIsProperty,
        int TV,
        long iI,
        boolean _boolean,
        String newProperty,
        String if_
    ) {
        this.b = b;
        this.isProperty = isProperty;
        this.isObjectIsProperty = isObjectIsProperty;
        this.hideProperty = hideProperty;
        this.ABC = ABC;
        this.aBC = aBC;
        this._Short = _Short;
        this.d = d;
        this.f = f;
        this.me = me;
        this.while_ = while_;
        this.objectIsProperty = objectIsProperty;
        this.TV = TV;
        this.iI = iI;
        this._boolean = _boolean;
        this.newProperty = newProperty;
        this.if_ = if_;
    }

    public InheritedBeanView(InheritedBeanView source) {
        this.b = source.b;
        this.isProperty = source.isProperty;
        this.isObjectIsProperty = source.isObjectIsProperty;
        this.hideProperty = source.hideProperty;
        this.ABC = source.ABC;
        this.aBC = source.aBC;
        this._Short = source._Short;
        this.d = source.d;
        this.f = source.f;
        this.me = source.me;
        this.while_ = source.while_;
        this.objectIsProperty = source.objectIsProperty;
        this.TV = source.TV;
        this.iI = source.iI;
        this._boolean = source._boolean;
        this.newProperty = source.newProperty;
        this.if_ = source.if_;
    }

    public InheritedBeanView(InheritedBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.InheritedBeanView should not be null.");
        }
        this.b = source.b;
        this.isProperty = source.isIsProperty();
        this.isObjectIsProperty = source.isObjectIsProperty;
        this.hideProperty = source.hideProperty;
        this.ABC = source.ABC;
        this.aBC = source.aBC;
        this._Short = source._Short;
        this.d = source.getD();
        this.f = source.getF();
        this.me = source.isMe();
        this.while_ = source.getWhile();
        this.objectIsProperty = source.isObjectIsProperty();
        this.TV = source.getTV();
        this.iI = source.getiI();
        this._boolean = source.is_boolean();
        this.newProperty = source.getNewProperty();
        this.if_ = source.getIf();
    }

    public static InheritedBeanView read(InheritedBean source) {
        if (source == null) {
            return null;
        }
        return new InheritedBeanView(source);
    }

    public static InheritedBeanView[] read(InheritedBean[] sources) {
        if (sources == null) {
            return null;
        }
        InheritedBeanView[] results = new InheritedBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<InheritedBeanView> read(List<InheritedBean> sources) {
        if (sources == null) {
            return null;
        }
        List<InheritedBeanView> results = new ArrayList<>();
        for (InheritedBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<InheritedBeanView> read(Set<InheritedBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<InheritedBeanView> results = new HashSet<>();
        for (InheritedBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<InheritedBeanView> read(Stack<InheritedBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<InheritedBeanView> results = new Stack<>();
        for (InheritedBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, InheritedBeanView> read(Map<K, InheritedBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, InheritedBeanView> results = new HashMap<>();
        for (Map.Entry<K, InheritedBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public int getB() {
        return this.b;
    }

    public boolean isIsProperty() {
        return this.isProperty;
    }

    public Boolean getIsObjectIsProperty() {
        return this.isObjectIsProperty;
    }

    public String getHideProperty() {
        return this.hideProperty;
    }

    public String getABC() {
        return this.ABC;
    }

    public Date getaBC() {
        return this.aBC;
    }

    public short get_Short() {
        return this._Short;
    }

    public short getD() {
        return this.d;
    }

    public AObject getF() {
        return this.f;
    }

    public boolean isMe() {
        return this.me;
    }

    public Date getWhile() {
        return this.while_;
    }

    public boolean isObjectIsProperty() {
        return this.objectIsProperty;
    }

    public int getTV() {
        return this.TV;
    }

    public long getiI() {
        return this.iI;
    }

    public boolean is_boolean() {
        return this._boolean;
    }

    public String getNewProperty() {
        return this.newProperty;
    }

    public String getIf() {
        return this.if_;
    }

}
