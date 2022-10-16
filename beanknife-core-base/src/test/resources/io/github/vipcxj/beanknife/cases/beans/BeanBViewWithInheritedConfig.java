package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = BeanB.class, configClass = InheritedConfigBeanBViewConfig.class)
public class BeanBViewWithInheritedConfig {

    private String a;

    private BeanA beanA;

    private BeanA anotherBeanA;

    private String type;

    private Date timestamp;

    public BeanBViewWithInheritedConfig() { }

    public BeanBViewWithInheritedConfig(
        String a,
        BeanA beanA,
        BeanA anotherBeanA,
        String type,
        Date timestamp
    ) {
        this.a = a;
        this.beanA = beanA;
        this.anotherBeanA = anotherBeanA;
        this.type = type;
        this.timestamp = timestamp;
    }

    public BeanBViewWithInheritedConfig(BeanBViewWithInheritedConfig source) {
        this.a = source.a;
        this.beanA = source.beanA;
        this.anotherBeanA = source.anotherBeanA;
        this.type = source.type;
        this.timestamp = source.timestamp;
    }

    public BeanBViewWithInheritedConfig(BeanB source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.BeanBViewWithInheritedConfig should not be null.");
        }
        this.a = source.getA();
        this.beanA = source.getBeanA();
        this.anotherBeanA = source.getAnotherBeanA();
        this.type = InheritedConfigBeanBViewConfig.type(source);
        this.timestamp = InheritedConfigBeanBViewConfig.timestamp();
    }

    public static BeanBViewWithInheritedConfig read(BeanB source) {
        if (source == null) {
            return null;
        }
        return new BeanBViewWithInheritedConfig(source);
    }

    public static BeanBViewWithInheritedConfig[] read(BeanB[] sources) {
        if (sources == null) {
            return null;
        }
        BeanBViewWithInheritedConfig[] results = new BeanBViewWithInheritedConfig[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<BeanBViewWithInheritedConfig> read(List<BeanB> sources) {
        if (sources == null) {
            return null;
        }
        List<BeanBViewWithInheritedConfig> results = new ArrayList<>();
        for (BeanB source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<BeanBViewWithInheritedConfig> read(Set<BeanB> sources) {
        if (sources == null) {
            return null;
        }
        Set<BeanBViewWithInheritedConfig> results = new HashSet<>();
        for (BeanB source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<BeanBViewWithInheritedConfig> read(Stack<BeanB> sources) {
        if (sources == null) {
            return null;
        }
        Stack<BeanBViewWithInheritedConfig> results = new Stack<>();
        for (BeanB source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, BeanBViewWithInheritedConfig> read(Map<K, BeanB> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, BeanBViewWithInheritedConfig> results = new HashMap<>();
        for (Map.Entry<K, BeanB> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public String getA() {
        return this.a;
    }

    public BeanA getBeanA() {
        return this.beanA;
    }

    public BeanA getAnotherBeanA() {
        return this.anotherBeanA;
    }

    public String getType() {
        return this.type;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getTypeWithTimestamp() {
        return InheritedConfigBeanBViewConfig.typeWithTimestamp(this.type, this.timestamp);
    }

    public Date getToday() {
        return InheritedConfigBeanBViewConfig.today();
    }

    public Date getYesterday() {
        return InheritedConfigBeanBViewConfig.yesterday();
    }

    public Date getTomorrow() {
        return InheritedConfigBeanBViewConfig.tomorrow();
    }

}
