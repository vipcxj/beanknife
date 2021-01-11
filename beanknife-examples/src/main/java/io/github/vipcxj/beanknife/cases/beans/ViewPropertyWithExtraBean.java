package io.github.vipcxj.beanknife.cases.beans;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewPropertyWithExtraBean {
    private SimpleBean a;
    private Map<String, List<SimpleBean[]>> b;
    private Set<SimpleBean> c;

    public SimpleBean getA() {
        return a;
    }

    public Map<String, List<SimpleBean[]>> getB() {
        return b;
    }

    public Set<SimpleBean> getC() {
        return c;
    }
}
