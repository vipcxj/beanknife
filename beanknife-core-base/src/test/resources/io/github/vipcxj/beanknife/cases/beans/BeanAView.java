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

@GeneratedView(targetClass = BeanA.class, configClass = ConfigBeanA.class)
public class BeanAView {

    private int a;

    private long b;

    private Date c;

    private Map<String, List<BeanBView>> beanBMap;

    private boolean d;

    public BeanAView() { }

    public BeanAView(
        int a,
        long b,
        Date c,
        Map<String, List<BeanBView>> beanBMap,
        boolean d
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.beanBMap = beanBMap;
        this.d = d;
    }

    public BeanAView(BeanAView source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
        this.beanBMap = source.beanBMap;
        this.d = source.d;
    }

    public BeanAView(BeanA source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.BeanAView should not be null.");
        }
        Map<String, List<BeanBView>> p0 = new HashMap<>();
        for (Map.Entry<String, List<BeanB>> el0 : source.getBeanBMap().entrySet()) {
            List<BeanBView> result0 = new ArrayList<>();
            for (BeanB el1 : el0.getValue()) {
                BeanBView result1 = BeanBView.read(el1);
                result0.add(result1);
            }
            p0.put(el0.getKey(), result0);
        }
        this.a = source.a;
        this.b = source.b;
        this.c = source.getC();
        this.beanBMap = p0;
        this.d = ConfigBeanA.d(source);
    }

    public static BeanAView read(BeanA source) {
        if (source == null) {
            return null;
        }
        return new BeanAView(source);
    }

    public static BeanAView[] read(BeanA[] sources) {
        if (sources == null) {
            return null;
        }
        BeanAView[] results = new BeanAView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<BeanAView> read(List<BeanA> sources) {
        if (sources == null) {
            return null;
        }
        List<BeanAView> results = new ArrayList<>();
        for (BeanA source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<BeanAView> read(Set<BeanA> sources) {
        if (sources == null) {
            return null;
        }
        Set<BeanAView> results = new HashSet<>();
        for (BeanA source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<BeanAView> read(Stack<BeanA> sources) {
        if (sources == null) {
            return null;
        }
        Stack<BeanAView> results = new Stack<>();
        for (BeanA source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, BeanAView> read(Map<K, BeanA> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, BeanAView> results = new HashMap<>();
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

    public Map<String, List<BeanBView>> getBeanBMap() {
        return this.beanBMap;
    }

    public boolean isD() {
        return this.d;
    }

    public String getE() {
        return ConfigBeanA.e(this.a, this.b);
    }

}
