package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = ViewPropertyContainerBean.class, configClass = ViewPropertyContainerBeanViewConfig.class)
public class ViewPropertyContainerBeanView {

    private long a;

    private String b;

    private ViewPropertyBeanWithoutParent view;

    private ViewPropertyBeanWithoutParent[] viewArray;

    private List<ViewPropertyBeanWithoutParent> viewList;

    private Set<ViewPropertyBeanWithoutParent> viewSet;

    private Map<String, ViewPropertyBeanWithoutParent> viewMap;

    private List<Map<String, ViewPropertyBeanWithoutParent>> viewMapList;

    private Map<String, List<ViewPropertyBeanWithoutParent>> viewListMap;

    private Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>> viewStackMapListMap;

    private Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>>[][][] viewStackMapListMapArrayArrayArray;

    public ViewPropertyContainerBeanView() { }

    public ViewPropertyContainerBeanView(
        long a,
        String b,
        ViewPropertyBeanWithoutParent view,
        ViewPropertyBeanWithoutParent[] viewArray,
        List<ViewPropertyBeanWithoutParent> viewList,
        Set<ViewPropertyBeanWithoutParent> viewSet,
        Map<String, ViewPropertyBeanWithoutParent> viewMap,
        List<Map<String, ViewPropertyBeanWithoutParent>> viewMapList,
        Map<String, List<ViewPropertyBeanWithoutParent>> viewListMap,
        Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>> viewStackMapListMap,
        Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>>[][][] viewStackMapListMapArrayArrayArray
    ) {
        this.a = a;
        this.b = b;
        this.view = view;
        this.viewArray = viewArray;
        this.viewList = viewList;
        this.viewSet = viewSet;
        this.viewMap = viewMap;
        this.viewMapList = viewMapList;
        this.viewListMap = viewListMap;
        this.viewStackMapListMap = viewStackMapListMap;
        this.viewStackMapListMapArrayArrayArray = viewStackMapListMapArrayArrayArray;
    }

    public ViewPropertyContainerBeanView(ViewPropertyContainerBeanView source) {
        this.a = source.a;
        this.b = source.b;
        this.view = source.view;
        this.viewArray = source.viewArray;
        this.viewList = source.viewList;
        this.viewSet = source.viewSet;
        this.viewMap = source.viewMap;
        this.viewMapList = source.viewMapList;
        this.viewListMap = source.viewListMap;
        this.viewStackMapListMap = source.viewStackMapListMap;
        this.viewStackMapListMapArrayArrayArray = source.viewStackMapListMapArrayArrayArray;
    }

    public static ViewPropertyContainerBeanView read(ViewPropertyContainerBean source) {
        if (source == null) {
            return null;
        }
        ViewPropertyBeanWithoutParent p0 = ViewPropertyBeanWithoutParent.read(source.getView());
        ViewPropertyBeanWithoutParent[] p1 = new ViewPropertyBeanWithoutParent[source.getViewArray().length];
        for (int i0 = 0; i0 < source.getViewArray().length; ++i0) {
            ViewPropertyBean el0 = source.getViewArray()[i0];
            ViewPropertyBeanWithoutParent result0 = ViewPropertyBeanWithoutParent.read(el0);
            p1[i0] = result0;
        }
        List<ViewPropertyBeanWithoutParent> p2 = new ArrayList<>();
        for (ViewPropertyBean el0 : source.getViewList()) {
            ViewPropertyBeanWithoutParent result0 = ViewPropertyBeanWithoutParent.read(el0);
            p2.add(result0);
        }
        Set<ViewPropertyBeanWithoutParent> p3 = new HashSet<>();
        for (ViewPropertyBean el0 : source.getViewSet()) {
            ViewPropertyBeanWithoutParent result0 = ViewPropertyBeanWithoutParent.read(el0);
            p3.add(result0);
        }
        Map<String, ViewPropertyBeanWithoutParent> p4 = new HashMap<>();
        for (Map.Entry<String, ViewPropertyBean> el0 : source.getViewMap().entrySet()) {
            ViewPropertyBeanWithoutParent result0 = ViewPropertyBeanWithoutParent.read(el0.getValue());
            p4.put(el0.getKey(), result0);
        }
        List<Map<String, ViewPropertyBeanWithoutParent>> p5 = new ArrayList<>();
        for (Map<String, ViewPropertyBean> el0 : source.getViewMapList()) {
            Map<String, ViewPropertyBeanWithoutParent> result0 = new HashMap<>();
            for (Map.Entry<String, ViewPropertyBean> el1 : el0.entrySet()) {
                ViewPropertyBeanWithoutParent result1 = ViewPropertyBeanWithoutParent.read(el1.getValue());
                result0.put(el1.getKey(), result1);
            }
            p5.add(result0);
        }
        Map<String, List<ViewPropertyBeanWithoutParent>> p6 = new HashMap<>();
        for (Map.Entry<String, List<ViewPropertyBean>> el0 : source.getViewListMap().entrySet()) {
            List<ViewPropertyBeanWithoutParent> result0 = new ArrayList<>();
            for (ViewPropertyBean el1 : el0.getValue()) {
                ViewPropertyBeanWithoutParent result1 = ViewPropertyBeanWithoutParent.read(el1);
                result0.add(result1);
            }
            p6.put(el0.getKey(), result0);
        }
        Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>> p7 = new HashMap<>();
        for (Map.Entry<String, List<Map<Integer, Stack<ViewPropertyBean>>>> el0 : source.getViewStackMapListMap().entrySet()) {
            List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>> result0 = new ArrayList<>();
            for (Map<Integer, Stack<ViewPropertyBean>> el1 : el0.getValue()) {
                Map<Integer, Stack<ViewPropertyBeanWithoutParent>> result1 = new HashMap<>();
                for (Map.Entry<Integer, Stack<ViewPropertyBean>> el2 : el1.entrySet()) {
                    Stack<ViewPropertyBeanWithoutParent> result2 = new Stack<>();
                    for (ViewPropertyBean el3 : el2.getValue()) {
                        ViewPropertyBeanWithoutParent result3 = ViewPropertyBeanWithoutParent.read(el3);
                        result2.add(result3);
                    }
                    result1.put(el2.getKey(), result2);
                }
                result0.add(result1);
            }
            p7.put(el0.getKey(), result0);
        }
        Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>>[][][] p8 = new Map[source.getViewStackMapListMapArrayArrayArray().length][][];
        for (int i0 = 0; i0 < source.getViewStackMapListMapArrayArrayArray().length; ++i0) {
            Map<String, List<Map<Integer, Stack<ViewPropertyBean>>>>[][] el0 = source.getViewStackMapListMapArrayArrayArray()[i0];
            Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>>[][] result0 = new Map[el0.length][];
            for (int i1 = 0; i1 < el0.length; ++i1) {
                Map<String, List<Map<Integer, Stack<ViewPropertyBean>>>>[] el1 = el0[i1];
                Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>>[] result1 = new Map[el1.length];
                for (int i2 = 0; i2 < el1.length; ++i2) {
                    Map<String, List<Map<Integer, Stack<ViewPropertyBean>>>> el2 = el1[i2];
                    Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>> result2 = new HashMap<>();
                    for (Map.Entry<String, List<Map<Integer, Stack<ViewPropertyBean>>>> el3 : el2.entrySet()) {
                        List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>> result3 = new ArrayList<>();
                        for (Map<Integer, Stack<ViewPropertyBean>> el4 : el3.getValue()) {
                            Map<Integer, Stack<ViewPropertyBeanWithoutParent>> result4 = new HashMap<>();
                            for (Map.Entry<Integer, Stack<ViewPropertyBean>> el5 : el4.entrySet()) {
                                Stack<ViewPropertyBeanWithoutParent> result5 = new Stack<>();
                                for (ViewPropertyBean el6 : el5.getValue()) {
                                    ViewPropertyBeanWithoutParent result6 = ViewPropertyBeanWithoutParent.read(el6);
                                    result5.add(result6);
                                }
                                result4.put(el5.getKey(), result5);
                            }
                            result3.add(result4);
                        }
                        result2.put(el3.getKey(), result3);
                    }
                    result1[i2] = result2;
                }
                result0[i1] = result1;
            }
            p8[i0] = result0;
        }
        ViewPropertyContainerBeanView out = new ViewPropertyContainerBeanView();
        out.a = source.getA();
        out.b = source.getB();
        out.view = p0;
        out.viewArray = p1;
        out.viewList = p2;
        out.viewSet = p3;
        out.viewMap = p4;
        out.viewMapList = p5;
        out.viewListMap = p6;
        out.viewStackMapListMap = p7;
        out.viewStackMapListMapArrayArrayArray = p8;
        return out;
    }

    public static ViewPropertyContainerBeanView[] read(ViewPropertyContainerBean[] sources) {
        if (sources == null) {
            return null;
        }
        ViewPropertyContainerBeanView[] results = new ViewPropertyContainerBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<ViewPropertyContainerBeanView> read(List<ViewPropertyContainerBean> sources) {
        if (sources == null) {
            return null;
        }
        List<ViewPropertyContainerBeanView> results = new ArrayList<>();
        for (ViewPropertyContainerBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<ViewPropertyContainerBeanView> read(Set<ViewPropertyContainerBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<ViewPropertyContainerBeanView> results = new HashSet<>();
        for (ViewPropertyContainerBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<ViewPropertyContainerBeanView> read(Stack<ViewPropertyContainerBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<ViewPropertyContainerBeanView> results = new Stack<>();
        for (ViewPropertyContainerBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, ViewPropertyContainerBeanView> read(Map<K, ViewPropertyContainerBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, ViewPropertyContainerBeanView> results = new HashMap<>();
        for (Map.Entry<K, ViewPropertyContainerBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public long getA() {
        return this.a;
    }

    public String getB() {
        return this.b;
    }

    public ViewPropertyBeanWithoutParent getView() {
        return this.view;
    }

    public ViewPropertyBeanWithoutParent[] getViewArray() {
        return this.viewArray;
    }

    public List<ViewPropertyBeanWithoutParent> getViewList() {
        return this.viewList;
    }

    public Set<ViewPropertyBeanWithoutParent> getViewSet() {
        return this.viewSet;
    }

    public Map<String, ViewPropertyBeanWithoutParent> getViewMap() {
        return this.viewMap;
    }

    public List<Map<String, ViewPropertyBeanWithoutParent>> getViewMapList() {
        return this.viewMapList;
    }

    public Map<String, List<ViewPropertyBeanWithoutParent>> getViewListMap() {
        return this.viewListMap;
    }

    public Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>> getViewStackMapListMap() {
        return this.viewStackMapListMap;
    }

    public Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>>[][][] getViewStackMapListMapArrayArrayArray() {
        return this.viewStackMapListMapArrayArrayArray;
    }

}
