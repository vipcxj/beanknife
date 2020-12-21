package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.*;

import java.util.List;
import java.util.Map;

@ViewOf(value=BeanA.class, includes={BeanAMeta.a, BeanAMeta.b, BeanAMeta.c, BeanAMeta.beanBMap}) // (7)
public class ConfigBeanA {
    @OverrideViewProperty(BeanAMeta.beanBMap) // (8)
    private Map<String, List<BeanBView>> beanBMap;
    @NewViewProperty("d") // (9)
    public static boolean d(BeanA source) { // (10)
        return true; // Generally you should achieve the data from the source.
    }
    @NewViewProperty("e") // (11)
    @Dynamic // (12)
    public static String e(@InjectProperty int a, @InjectProperty long b) { // (13)
        return "" + a + b;
    }
}
