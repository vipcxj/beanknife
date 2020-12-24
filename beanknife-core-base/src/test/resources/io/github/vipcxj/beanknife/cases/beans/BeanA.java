package io.github.vipcxj.beanknife.cases.beans;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class BeanA {
    public int a;  // (1)
    protected long b; // (2)
    public String c; // (3)
    private boolean d; // (4)
    private Map<String, List<BeanB>> beanBMap;

    public Date getC() { // (3)
        return new Date();
    }

    // (5)
    public Map<String, List<BeanB>> getBeanBMap() {
        return beanBMap;
    }
}
