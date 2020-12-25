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

@GeneratedView(targetClass = BeanA.class, configClass = InheritedConfigBeanAViewConfig.class)
public class BeanAViewWithInheritedConfig {

    private int a;

    private long b;

    private Date c;

    private Map<String, List<BeanB>> beanBMap;

    private String type;

    private Date timestamp;

    public BeanAViewWithInheritedConfig() { }

    public BeanAViewWithInheritedConfig(
        int a,
        long b,
        Date c,
        Map<String, List<BeanB>> beanBMap,
        String type,
        Date timestamp
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.beanBMap = beanBMap;
        this.type = type;
        this.timestamp = timestamp;
    }

    public BeanAViewWithInheritedConfig(BeanAViewWithInheritedConfig source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
        this.beanBMap = source.beanBMap;
        this.type = source.type;
        this.timestamp = source.timestamp;
    }

    public static BeanAViewWithInheritedConfig read(BeanA source) {
        if (source == null) {
            return null;
        }
        BeanAViewWithInheritedConfig out = new BeanAViewWithInheritedConfig();
        out.a = source.a;
        out.b = source.b;
        out.c = source.getC();
        out.beanBMap = source.getBeanBMap();
        out.type = InheritedConfigBeanAViewConfig.type(source);
        out.timestamp = InheritedConfigBeanAViewConfig.timestamp();
        return out;
    }

    public static BeanAViewWithInheritedConfig[] read(BeanA[] sources) {
        if (sources == null) {
            return null;
        }
        BeanAViewWithInheritedConfig[] results = new BeanAViewWithInheritedConfig[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<BeanAViewWithInheritedConfig> read(List<BeanA> sources) {
        if (sources == null) {
            return null;
        }
        List<BeanAViewWithInheritedConfig> results = new ArrayList<>();
        for (BeanA source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<BeanAViewWithInheritedConfig> read(Set<BeanA> sources) {
        if (sources == null) {
            return null;
        }
        Set<BeanAViewWithInheritedConfig> results = new HashSet<>();
        for (BeanA source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<BeanAViewWithInheritedConfig> read(Stack<BeanA> sources) {
        if (sources == null) {
            return null;
        }
        Stack<BeanAViewWithInheritedConfig> results = new Stack<>();
        for (BeanA source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, BeanAViewWithInheritedConfig> read(Map<K, BeanA> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, BeanAViewWithInheritedConfig> results = new HashMap<>();
        for (Map.Entry<K, BeanA> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public int getA() {
        return this.a;
    }

    public long getB() {
        return this.b;
    }

    public Date getC() {
        return this.c;
    }

    public Map<String, List<BeanB>> getBeanBMap() {
        return this.beanBMap;
    }

    public String getType() {
        return this.type;
    }

    public Date getTimestamp() {
        return this.timestamp;
    }

    public String getTypeWithTimestamp() {
        return InheritedConfigBeanAViewConfig.typeWithTimestamp(this.type, this.timestamp);
    }

    public Date getToday() {
        return InheritedConfigBeanAViewConfig.today();
    }

    public Date getYesterday() {
        return InheritedConfigBeanAViewConfig.yesterday();
    }

    public Date getTomorrow() {
        return InheritedConfigBeanAViewConfig.tomorrow();
    }

}
