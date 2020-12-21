BeanKnife
===============
A annotation processor library to automatically generate the data transfer objects (DTO).

## Docs
* [Introduction](#introduction)
* [Basics](#basics)
* [Advanced Usage](#advanced-usage)

### Introduction
What's the problem?

When writing a web service, we always need to transmit data to the client. 
We generally call this data objects as DTO(Data Transfer Object. 
Many of them have similar formats and are derived from some internal data objects of the server, such as database entity classes. 
But according to the specific needs of the service, they are slightly different. 
Writing the corresponding DTO for each service is boring and tedious. 
And in many cases, DTO needs to be maintained synchronously with the corresponding server internal classes. 
For example, when the internal class of the server has a new attribute, the corresponding DTO will also need the corresponding attribute. 
Maintaining this relationship manually is tedious and error-prone. 
So many people simply omit the DTO and use the server internal classes directly. 
But most of the time this is not a good practice. 
Server internal classes often have many and complete attributes, but a service may not need so many attributes. 
So this will bring additional bandwidth consumption. 
To make matters worse, DTO must be serializable, such as converting to json. 
But some internal classes are difficult to serialize. 
For example, there are circular references, or very large deep references. 
The workaround for many people is to customize its serialization process by configuring the json library. 
But even jackson, the most widely used json library in the java world, is not doing very well in this regard. 
Based on the above facts, I brought you this library <b>BeanKnife</b>

What can <b>BeanKnife</b> do for you?

Basically, <b>BeanKnife</b> will generate the DTO automatically for you.
You just need to tell the library which you need and which you not need by annotation.
Furthermore, it has these powerful features:
1. Automatically generate meta class which include all the property names of the target class. 
You can use these property names in the configure annotation to avoid spelling mistakes.
2. This is a non-invasive library. 
You can let the target class alone and put the annotation on any other class.
Of course, directly put the annotation on the target class is also supported.
3. You can define the new property in the DTO class. 
It should be configured on the class other than the target class.
4. Support converter, which convert from one type to another type or a type to itself.
The runtime library provide some built-in converters. Such as converter a number object to zero when it is null.
5. You can override the exist property.
It should be configured on the class other than the target class.
You can change the property type by converter or replace it with the DTO version. 
Furthermore, you can rewrite the property.
6. Automatically convert the property to its DTO version, when you override it with the DTO type.
The convert feature support Array, List, Set, Stack and Map of the DTO too. And it even support more complex combination such as `List<Map<String, Set<DTO>[]>>[][]`

### Basics
```java
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;

@ViewOf(includePattern=".*") // (1)
public class SimpleBean {
    private String a;
    private Integer b;
    private long c;

    public String getA() {
        return a;
    }

    public Integer getB() {
        return b;
    }

    public long getC() {
        return c;
    }
}
```
1. Annotated the class with `@ViewOf` and set the appropriate attribute `includePattern`. 
Then the annotation processor will do what it should do. The attribute `includePattern` is regex pattern, means which properties are included. 
By default, nothing is included. 
So `includePattern=".*"` is necessary here, or the generated class will has no properties.

Above configure will generate:
```java
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedMeta;

@GeneratedMeta(
    targetClass = SimpleBean.class,
    configClass = SimpleBean.class,
    proxies = {
        SimpleBean.class
    }
)
public class SimpleBeanMeta {
    public static final String a = "a";
    public static final String b = "b";
    public static final String c = "c";
}

```
and
```java
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@GeneratedView(targetClass = SimpleBean.class, configClass = SimpleBean.class)
public class SimpleBeanView {

    private String a;

    private Integer b;

    private long c;

    public SimpleBeanView() { }

    public SimpleBeanView(
        String a,
        Integer b,
        long c
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public SimpleBeanView(SimpleBeanView source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
    }

    public static SimpleBeanView read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        SimpleBeanView out = new SimpleBeanView();
        out.a = source.getA();
        out.b = source.getB();
        out.c = source.getC();
        return out;
    }

    public static SimpleBeanView[] read(SimpleBean[] sources) {
        if (sources == null) {
            return null;
        }
        SimpleBeanView[] results = new SimpleBeanView[sources.length];
        for (int i = 0; i < sources.length; ++i) {
            results[i] = read(sources[i]);
        }
        return results;
    }

    public static List<SimpleBeanView> read(List<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        List<SimpleBeanView> results = new ArrayList<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Set<SimpleBeanView> read(Set<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Set<SimpleBeanView> results = new HashSet<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static Stack<SimpleBeanView> read(Stack<SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Stack<SimpleBeanView> results = new Stack<>();
        for (SimpleBean source : sources) {
            results.add(read(source));
        }
        return results;
    }

    public static <K> Map<K, SimpleBeanView> read(Map<K, SimpleBean> sources) {
        if (sources == null) {
            return null;
        }
        Map<K, SimpleBeanView> results = new HashMap<>();
        for (Map.Entry<K, SimpleBean> source : sources.entrySet()) {
            results.put(source.getKey(), read(source.getValue()));
        }
        return results;
    }

    public String getA() {
        return this.a;
    }

    public Integer getB() {
        return this.b;
    }

    public long getC() {
        return this.c;
    }

}
```
`SimpleBeanMeta` is the meta bean which list all the available property in `SimpleBean`. 
Here 'available' means all properties except private properties will be listed.
The protected and package properties are included as well because they can be accessed by the classes in same package.
The usage of the generated meta bean will be shown in next example.

`SimpleBeanView` is the generated DTO class. 
It always has a static `read` method to create a instance of itself from the original bean.
which is `public static SimpleBeanView read(SimpleBean source)` here.
It also will has all the properties configured in the `@viewOf` annotation. 
In this example, it inherit all the properties from `SimpleBean`.
By default. the DTO bean is readonly. So all properties of it have not setter method. 
Of course, you can change this behaviour.

Here is another more complex example.
```java
// ignore all imports
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
```
```java
public class BeanB { // (6)
    private String a;
    private BeanA beanA;

    public String getA() {
        return a;
    }
    public BeanA getBeanA() {
        return beanA;
    }
}
```
```java
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
```
```java
@ViewOf(value=BeanB.class, includes={BeanBMeta.a}) // (14)
public class ConfigBeanB {
}
```
`BeanA` is a native java bean, It has not any annotation from this library. 
We configure it to generate the DTO by annotated a configure class `ConfigBeanA`.
1. The field `a` is public, and there is no getter method named `getA`, so it is selected as an available property.
2. The field `b` is protected, and there is no getter method named `getB`, so it is selected as an available property.
3. The field `c` is public, however there is also a getter method named `getC`.
 In this library, the getter method always has a high priority. 
 So the field `c` is ignored, and the getter method `getC` is selected as an available property. 
 As a result the property `c` is a `Date`.
4. The field `d` is private, it can not be accessed by any other class. So it is ignored.
5. The getter method `getBeanBMap` is selected as an available property `beanBMap`. 
Note that it's type `BeanB` also has a related DTO type `BeanBView`.
6. Another bean. we will configure it later.
7. Rather than configure `BeanA` directly, this time we annotate a config class called `ConfigBeanA` with `@ViewOf`.
 Because `ConfigBeanA` is not the target class, we use the attribute `value` to tell the library the information of the target class.
 With the annotation `ViewOf`, the library will generate the meta class `BeanAMeta` and the DTO Class `BeanAView`.
 Here we use the attribute `includes` instead of `includePattern` to specialize the included properties. 
 And the meta class `BeanAMeta` is used here.
 Although at this time, the class `BeanAMeta` still not generated and the compiler may complain that `BeanAMeta.xxx` is not a valid symbol, just use it.
 All will be ok after compile the whole project. If you really hate the error shown by IDE, you can annotated the `BeanB` with `@ViewOf` or `@ViewMeta` and compile it. 
 Then the meta class `BeanBMeta` will be generated. Then the error will be gone.
8. If we want to change a exist property, we should use `@OverrideViewProperty`. 
Here we want to change the type of `beanBMap` to `Map<String, List<BeanBView>>`. 
It has the same shape with original type `Map<String, List<BeanB>>`. The only difference is `BeanB` become `BeanBView`.
Because `BeanBView` is the DTO version of `BeanB`, and the library can detect it. So the library has the ability to convert the type automatically.
No custom implementation is needed. So just use a field is enough. Or you have to define your own implementation with a method.
9. Here we add a new custom property named `d` to the generated DTO class. 
10. There is no `@dynamic` annotated, So it is a *static* custom property. 
The static property is initialized in the `read` method, so the implementation method may achieve the data direct from the target bean.
The implementation method should has the shape `public static PropertyType methodName(TargetBeanType source)`.
Here the `PropertyType` is `boolean`, the `methodName` is `d` and the `TargetBeanType` is `BeanA`.
11. Here we add a new custom property named `e` to the generated DTO class. 
12. `@Dynamic` means this custom property is a dynamic property. The dynamic property is calculated when called getter. 
So the implementation method is not able to achieve the data from the target bean.
13. The implementation method should has the shape `public static PropertyType methodName(@InjectProperty PropertyAType propertyAName, @InjectProperty PropertyBType propertyBName, ...)`.
We can use the annotation `@InjectProperty` to inject the property value of the DTO bean to the static method. Obviously, the parameter type should match the property type. 
But you can use any parameter name only if you have specialized the property name in the `@InjectProperty` with `value` attribute.
14. Configure to generate the meta class and DTO class of `BeanB` with only property `a`. So `beanA` is excluded.

The above class will generate these classes:

The meta class of `BeanA`
```java
public class BeanAMeta {
    public static final String a = "a";
    public static final String a = "b";
    public static final String a = "c";
    public static final String beanBMap = "beanBMap";
}
```
The meta class of `BeanB`
```java
public class BeanBMeta {
    public static final String a = "a";
    public static final String beanA = "beanA";
}
```
The DTO class of `BeanA`
```java
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

    public static BeanAView read(BeanA source) {
        if (source == null) {
            return null;
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
        BeanAView out = new BeanAView();
        out.a = source.a;
        out.b = source.b;
        out.c = source.getC();
        out.beanBMap = p0;
        out.d = ConfigBeanA.d(source);
        return out;
    }

    /* ... other read methods ... */

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
```
The DTO class of `BeanB`
```java
@GeneratedView(targetClass = BeanB.class, configClass = ConfigBeanB.class)
public class BeanBView {

    private String a;

    public BeanBView() { }

    public BeanBView(
        String a
    ) {
        this.a = a;
    }

    public BeanBView(BeanBView source) {
        this.a = source.a;
    }

    public static BeanBView read(BeanB source) {
        if (source == null) {
            return null;
        }
        BeanBView out = new BeanBView();
        out.a = source.getA();
        return out;
    }
    
    /* ... other read methods ... */
    
    public String getA() {
        return this.a;
    }

}
```

### Advanced Usage
WIP.