package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import io.github.vipcxj.beanknife.runtime.converters.NullIntegerAsZeroConverter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = SimpleBean.class, configClass = MapPropertiesViewConfigure.class)
public class MapPropertiesView {

    private String aMap;

    private int bMapWithConverter;

    private Date cMapUseMethod;

    public MapPropertiesView() { }

    public MapPropertiesView(
        String aMap,
        int bMapWithConverter,
        Date cMapUseMethod
    ) {
        this.aMap = aMap;
        this.bMapWithConverter = bMapWithConverter;
        this.cMapUseMethod = cMapUseMethod;
    }

    public MapPropertiesView(MapPropertiesView source) {
        this.aMap = source.aMap;
        this.bMapWithConverter = source.bMapWithConverter;
        this.cMapUseMethod = source.cMapUseMethod;
    }

    public MapPropertiesView(SimpleBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.MapPropertiesView should not be null.");
        }
        this.aMap = source.getA();
        this.bMapWithConverter = new NullIntegerAsZeroConverter().convert(source.getB());
        this.cMapUseMethod = MapPropertiesViewConfigure.cMapUseMethod(source);
    }

    public static MapPropertiesView read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        return new MapPropertiesView(source);
    }

    public static MapPropertiesView[] read(SimpleBean[] sources) {
        if (sources == null) {
            return null;
        }
        MapPropertiesView[] results = new MapPropertiesView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<MapPropertiesView> read(List<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        List<MapPropertiesView> results = new ArrayList<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<MapPropertiesView> read(Set<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<MapPropertiesView> results = new HashSet<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<MapPropertiesView> read(Stack<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<MapPropertiesView> results = new Stack<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, MapPropertiesView> read(Map<K, SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, MapPropertiesView> results = new HashMap<>();
        for (Map.Entry<K, SimpleBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public String getA() {
        return this.aMap;
    }

    public int getB() {
        return this.bMapWithConverter;
    }

    public Date getC() {
        return this.cMapUseMethod;
    }

}
