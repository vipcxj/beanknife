package io.github.vipcxj.beanknife.cases.beans;

import io.github.vipcxj.beanknife.runtime.BeanProviders;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import io.github.vipcxj.beanknife.runtime.utils.BeanUsage;
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

    private int newIntFieldProperty;

    private int newIntMethodProperty;

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
        Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>>[][][] viewStackMapListMapArrayArrayArray,
        int newIntFieldProperty,
        int newIntMethodProperty
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
        this.newIntFieldProperty = newIntFieldProperty;
        this.newIntMethodProperty = newIntMethodProperty;
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
        this.newIntFieldProperty = source.newIntFieldProperty;
        this.newIntMethodProperty = source.newIntMethodProperty;
    }

    public ViewPropertyContainerBeanView(ViewPropertyContainerBean source, int newIntFieldProperty) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.ViewPropertyContainerBeanView should not be null.");
        }
        ViewPropertyBeanWithoutParent p0 = ViewPropertyBeanWithoutParent.read(source.getView());
        ViewPropertyBeanWithoutParent[] p1;
        if (source.getViewArray() != null) {
            p1 = new ViewPropertyBeanWithoutParent[source.getViewArray().length];
            for (int i0 = 0; i0 < source.getViewArray().length; ++i0) {
                ViewPropertyBean el0 = source.getViewArray()[i0];
                ViewPropertyBeanWithoutParent result0 = ViewPropertyBeanWithoutParent.read(el0);
                p1[i0] = result0;
            }
        } else {
            p1 = null;
        }
        List<ViewPropertyBeanWithoutParent> p2;
        if (source.getViewList() != null) {
            p2 = new ArrayList<>();
            for (ViewPropertyBean el0 : source.getViewList()) {
                ViewPropertyBeanWithoutParent result0 = ViewPropertyBeanWithoutParent.read(el0);
                p2.add(result0);
            }
        } else {
            p2 = null;
        }
        Set<ViewPropertyBeanWithoutParent> p3;
        if (source.getViewSet() != null) {
            p3 = new HashSet<>();
            for (ViewPropertyBean el0 : source.getViewSet()) {
                ViewPropertyBeanWithoutParent result0 = ViewPropertyBeanWithoutParent.read(el0);
                p3.add(result0);
            }
        } else {
            p3 = null;
        }
        Map<String, ViewPropertyBeanWithoutParent> p4;
        if (source.getViewMap() != null) {
            p4 = new HashMap<>();
            for (Map.Entry<String, ViewPropertyBean> el0 : source.getViewMap().entrySet()) {
                ViewPropertyBeanWithoutParent result0 = ViewPropertyBeanWithoutParent.read(el0.getValue());
                p4.put(el0.getKey(), result0);
            }
        } else {
            p4 = null;
        }
        List<Map<String, ViewPropertyBeanWithoutParent>> p5;
        if (source.getViewMapList() != null) {
            p5 = new ArrayList<>();
            for (Map<String, ViewPropertyBean> el0 : source.getViewMapList()) {
                Map<String, ViewPropertyBeanWithoutParent> result0;
                if (el0 != null) {
                    result0 = new HashMap<>();
                    for (Map.Entry<String, ViewPropertyBean> el1 : el0.entrySet()) {
                        ViewPropertyBeanWithoutParent result1 = ViewPropertyBeanWithoutParent.read(el1.getValue());
                        result0.put(el1.getKey(), result1);
                    }
                } else {
                    result0 = null;
                }
                p5.add(result0);
            }
        } else {
            p5 = null;
        }
        Map<String, List<ViewPropertyBeanWithoutParent>> p6;
        if (source.getViewListMap() != null) {
            p6 = new HashMap<>();
            for (Map.Entry<String, List<ViewPropertyBean>> el0 : source.getViewListMap().entrySet()) {
                List<ViewPropertyBeanWithoutParent> result0;
                if (el0.getValue() != null) {
                    result0 = new ArrayList<>();
                    for (ViewPropertyBean el1 : el0.getValue()) {
                        ViewPropertyBeanWithoutParent result1 = ViewPropertyBeanWithoutParent.read(el1);
                        result0.add(result1);
                    }
                } else {
                    result0 = null;
                }
                p6.put(el0.getKey(), result0);
            }
        } else {
            p6 = null;
        }
        Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>> p7;
        if (source.getViewStackMapListMap() != null) {
            p7 = new HashMap<>();
            for (Map.Entry<String, List<Map<Integer, Stack<ViewPropertyBean>>>> el0 : source.getViewStackMapListMap().entrySet()) {
                List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>> result0;
                if (el0.getValue() != null) {
                    result0 = new ArrayList<>();
                    for (Map<Integer, Stack<ViewPropertyBean>> el1 : el0.getValue()) {
                        Map<Integer, Stack<ViewPropertyBeanWithoutParent>> result1;
                        if (el1 != null) {
                            result1 = new HashMap<>();
                            for (Map.Entry<Integer, Stack<ViewPropertyBean>> el2 : el1.entrySet()) {
                                Stack<ViewPropertyBeanWithoutParent> result2;
                                if (el2.getValue() != null) {
                                    result2 = new Stack<>();
                                    for (ViewPropertyBean el3 : el2.getValue()) {
                                        ViewPropertyBeanWithoutParent result3 = ViewPropertyBeanWithoutParent.read(el3);
                                        result2.add(result3);
                                    }
                                } else {
                                    result2 = null;
                                }
                                result1.put(el2.getKey(), result2);
                            }
                        } else {
                            result1 = null;
                        }
                        result0.add(result1);
                    }
                } else {
                    result0 = null;
                }
                p7.put(el0.getKey(), result0);
            }
        } else {
            p7 = null;
        }
        Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>>[][][] p8;
        if (source.getViewStackMapListMapArrayArrayArray() != null) {
            p8 = new Map[source.getViewStackMapListMapArrayArrayArray().length][][];
            for (int i0 = 0; i0 < source.getViewStackMapListMapArrayArrayArray().length; ++i0) {
                Map<String, List<Map<Integer, Stack<ViewPropertyBean>>>>[][] el0 = source.getViewStackMapListMapArrayArrayArray()[i0];
                Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>>[][] result0;
                if (el0 != null) {
                    result0 = new Map[el0.length][];
                    for (int i1 = 0; i1 < el0.length; ++i1) {
                        Map<String, List<Map<Integer, Stack<ViewPropertyBean>>>>[] el1 = el0[i1];
                        Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>>[] result1;
                        if (el1 != null) {
                            result1 = new Map[el1.length];
                            for (int i2 = 0; i2 < el1.length; ++i2) {
                                Map<String, List<Map<Integer, Stack<ViewPropertyBean>>>> el2 = el1[i2];
                                Map<String, List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>>> result2;
                                if (el2 != null) {
                                    result2 = new HashMap<>();
                                    for (Map.Entry<String, List<Map<Integer, Stack<ViewPropertyBean>>>> el3 : el2.entrySet()) {
                                        List<Map<Integer, Stack<ViewPropertyBeanWithoutParent>>> result3;
                                        if (el3.getValue() != null) {
                                            result3 = new ArrayList<>();
                                            for (Map<Integer, Stack<ViewPropertyBean>> el4 : el3.getValue()) {
                                                Map<Integer, Stack<ViewPropertyBeanWithoutParent>> result4;
                                                if (el4 != null) {
                                                    result4 = new HashMap<>();
                                                    for (Map.Entry<Integer, Stack<ViewPropertyBean>> el5 : el4.entrySet()) {
                                                        Stack<ViewPropertyBeanWithoutParent> result5;
                                                        if (el5.getValue() != null) {
                                                            result5 = new Stack<>();
                                                            for (ViewPropertyBean el6 : el5.getValue()) {
                                                                ViewPropertyBeanWithoutParent result6 = ViewPropertyBeanWithoutParent.read(el6);
                                                                result5.add(result6);
                                                            }
                                                        } else {
                                                            result5 = null;
                                                        }
                                                        result4.put(el5.getKey(), result5);
                                                    }
                                                } else {
                                                    result4 = null;
                                                }
                                                result3.add(result4);
                                            }
                                        } else {
                                            result3 = null;
                                        }
                                        result2.put(el3.getKey(), result3);
                                    }
                                } else {
                                    result2 = null;
                                }
                                result1[i2] = result2;
                            }
                        } else {
                            result1 = null;
                        }
                        result0[i1] = result1;
                    }
                } else {
                    result0 = null;
                }
                p8[i0] = result0;
            }
        } else {
            p8 = null;
        }
        ViewPropertyContainerBeanViewConfig configureBean = BeanProviders.INSTANCE.get(ViewPropertyContainerBeanViewConfig.class, BeanUsage.CONFIGURE, source, false, false);
        this.a = source.getA();
        this.b = source.getB();
        this.view = p0;
        this.viewArray = p1;
        this.viewList = p2;
        this.viewSet = p3;
        this.viewMap = p4;
        this.viewMapList = p5;
        this.viewListMap = p6;
        this.viewStackMapListMap = p7;
        this.viewStackMapListMapArrayArrayArray = p8;
        this.newIntFieldProperty = newIntFieldProperty;
        this.newIntMethodProperty = configureBean.newIntMethodProperty();
    }

    public static ViewPropertyContainerBeanView read(ViewPropertyContainerBean source, int newIntFieldProperty) {
        if (source == null) {
            return null;
        }
        return new ViewPropertyContainerBeanView(source, newIntFieldProperty);
    }

    public void writeBack(ViewPropertyContainerBean target) {
        ViewPropertyBean p0 = this.view.createAndWriteBack();
        List<ViewPropertyBean> p1;
        if (this.viewList != null) {
            p1 = new ArrayList<>();
            for (ViewPropertyBeanWithoutParent el0 : this.viewList) {
                ViewPropertyBean result0 = el0.createAndWriteBack();
                p1.add(result0);
            }
        } else {
            p1 = null;
        }

        target.setView(p0);
        target.setViewList(p1);
    }

    public ViewPropertyContainerBean createAndWriteBack() {
        ViewPropertyBean p0 = this.view.createAndWriteBack();
        List<ViewPropertyBean> p1;
        if (this.viewList != null) {
            p1 = new ArrayList<>();
            for (ViewPropertyBeanWithoutParent el0 : this.viewList) {
                ViewPropertyBean result0 = el0.createAndWriteBack();
                p1.add(result0);
            }
        } else {
            p1 = null;
        }
        ViewPropertyContainerBean target = BeanProviders.INSTANCE.get(ViewPropertyContainerBean.class, BeanUsage.CONVERT_BACK, this, false, false);
        target.setView(p0);
        target.setViewList(p1);
        return target;
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

    public int getNewIntFieldProperty() {
        return this.newIntFieldProperty;
    }

    public int getNewIntMethodProperty() {
        return this.newIntMethodProperty;
    }

}
