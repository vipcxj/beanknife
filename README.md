BeanKnife
===============

[![Maven Release][maven-shield]][maven-link]

[中文版](/README_CN.md)

An annotation processor library to automatically generate the data transfer objects (DTO).

## Docs
* [Quick Look](#quick-look)
* [Requirement](#requirement)
* [Quick Start](#quick-start)
* [Introduction](#introduction)
* [Basics](#basics)
* [Advanced Usage](#advanced-usage)
  * [Inheritance of configuration](#Inheritance-of-configuration)
  * [Inheritance of annotation](#Inheritance-of-annotation)
  * [change the generated class name or package](#change-the-generated-class-name)
  * [filter the property](#filter-the-property)
  * [generate the setter method](#generate-the-setter-method)
  * [use non-static method to define new properties](#use-non-static-method-to-define-new-properties)
  * [spring support](#spring-support)
  * [serializable support](#serializable-support)
  
### Quick Look
Base on
```java
class Pojo1 {
   private int a;
   private List<Pojo2> pojo2List;
}

class Pojo2 {
    private String b;
    private Pojo1 pojo1;
}
```
Beanknife is able to generate
```java
class Pojo1View1 {
   private int a;
   private List<Pojo2View1> pojo2List;
}

class Pojo2View1 {
    private String b;
}
```
Or
```java
class Pojo2View2 {
    private String b;
    private Pojo1View2 pojo1;
}

class Pojo1View2 {
   private int a;
}

```

### Requirement
Jdk 1.8+ (include jdk 1.8)

### Quick Start
This library is an annotation processor. Just use it like any other annotation processor.

You will need beanknife-runtime-${version}.jar in your runtime classpath, 
and you will need beanknife-core-${version}.jar in your annotation-processor classpath.
In fact, only The `PropertyConverter` interface in the beanknife-runtime-${version}.jar need be in runtime classpath, 
all others just need in compile classpath. In the future, I may split them.

In Maven, you can write:
```xml
<dependencies>
  <dependency>
    <groupId>io.github.vipcxj</groupId>
    <artifactId>beanknife-runtime</artifactId>
    <version>${beanknife.version}</version>
  </dependency>
</dependencies>
```
```xml
<plugins>
  <plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
      <annotationProcessorPaths>
        <path>
          <groupId>io.github.vipcxj</groupId>
          <artifactId>beanknife-core</artifactId>
          <version>${beanknife.version}</version>
        </path>
      </annotationProcessorPaths>
    </configuration>
  </plugin>
</plugins>
```

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
The runtime library provide some built-in converters. Such as converting a number object to zero when it is null.
5. You can override the existed property.
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

#### Inheritance of configuration
Although `@ViewOf` cannot be inherited, many other configuration elements can be inherited.
Such as the configuration bean itself.
See [Leaf11BeanViewConfigure](/beanknife-examples/src/main/java/io/github/vipcxj/beanknife/cases/beans/Leaf11BeanViewConfigure.java), 
[Leaf12BeanViewConfigure](/beanknife-examples/src/main/java/io/github/vipcxj/beanknife/cases/beans/Leaf12BeanViewConfigure.java) 
and [Leaf21BeanViewConfigure](/beanknife-examples/src/main/java/io/github/vipcxj/beanknife/cases/beans/Leaf21BeanViewConfigure.java)

Almost all attributes of `@ViewOf` has a standalone version annotation. They all can be inherited.

| attribute               | standalone annotation           | merge method  | value on child  | value on base    | final value                 |
|-------------------------|---------------------------------|---------------|-----------------|------------------|-----------------------------|
| `access`                | `ViewAccess`                    | override      | `Access.NONE`   | `Access.PUBLIC`  | `Access.NONE`               |
| `includes`              | `ViewPropertiesInclude`         | union         | `{"a", "b"}`    | `{"b", "c"}`     | `{"b", "c", "a", "b"}`      | 
| `excludes`              | `ViewPropertiesExclude`         | union         | `{"a", "b"}`    | `{"b", "c"}`     | `{"b", "c", "a", "b"}`      | 
| `includePattern`        | `ViewPropertiesIncludePattern`  | append        | `"[aA]pple\\d"` | `"[oO]range\\d"` | `"[oO]range\\d [aA]pple\\d"`|
| `excludePattern`        | `ViewPropertiesExcludePattern`  | append        | `"[aA]pple\\d"` | `"[oO]range\\d"` | `"[oO]range\\d [aA]pple\\d"`|
| `emptyConstructor`      | `ViewEmptyConstructor`          | override      | `Access.NONE`   | `Access.PUBLIC`  | `Access.NONE`               |
| `fieldsConstructor`     | `ViewFieldsConstructor`         | override      | `Access.NONE`   | `Access.PUBLIC`  | `Access.NONE`               |
| `copyConstructor`       | `ViewCopyConstructor`           | override      | `Access.NONE`   | `Access.PUBLIC`  | `Access.NONE`               |
| `readConstructor`       | `ViewReadConstructor`           | override      | `Access.NONE`   | `Access.PUBLIC`  | `Access.NONE`               |
| `getters`               | `ViewGetters`                   | override      | `Access.NONE`   | `Access.PUBLIC`  | `Access.NONE`               |
| `setters`               | `ViewSetters`                   | override      | `Access.NONE`   | `Access.PUBLIC`  | `Access.NONE`               |
| `errorMethods`          | `ViewErrorMethods`              | override      | `false`         | `true`           | `false`                     |
| `serializable`          | `ViewSerializable`              | override      | `false`         | `true`           | `false`                     |
| `serialVersionUID`      | `ViewSerialVersionUID`          | override      | `1L`            | `0L`             | `1L`                        |
| `useDefaultBeanProvider`| `ViewUseDefaultBeanProvider`    | override      | `false`         | `true`           | `false`                     |
| `configureBeanCacheType`| `ViewConfigureBeanCacheType`    | override      | `CacheType.NONE`| `CacheType.LOCAL`| `CacheType.NONE`            |

---
**NOTE**

The attribute of `ViewOf` has a higher priority than standalone annotation.

---

Through configuration inheritance, we can extract the common configuration into a single class, which will greatly simplify our configuration work.

#### Inheritance of annotation
We can use `@UseAnnotation` to make the generated class inheriting the annotations from configuration class and original class.
```java
@TypeAnnotation(Date.class)
@DocumentedTypeAnnotation(enumValue = AEnum.B, enumValues = {AEnum.C, AEnum.B, AEnum.A})
public class AnnotationBean extends BaseAnnotationBean {

    @FieldAnnotation1(doubleArray = 1.0)
    private String a;
    @FieldAnnotation2(annotation = @ValueAnnotation1(type = Date.class))
    private String b;
    @FieldAnnotation1(charValue = '0')
    @FieldAnnotation2(stringArray = "5")
    private String[] c;
    @FieldAnnotation1(annotations = {
            @ValueAnnotation1(),
            @ValueAnnotation1(type = AnnotationBean.class)
    })
    @PropertyAnnotation1
    private int d;
    @PropertyAnnotation2(stringValue = "field_e")
    private Date e;
    @PropertyAnnotation2(stringValue = "field_f")
    private List<String> f;
    @PropertyAnnotation1(stringValue = "field_g")
    private short g;

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String[] getC() {
        return c;
    }

    public void setC(String[] c) {
        this.c = c;
    }

    @MethodAnnotation1(
            charValue = 'a',
            annotations = {
                    @ValueAnnotation1,
                    @ValueAnnotation1(
                            annotations = @ValueAnnotation2
                    )
            }
    )
    public int getD() {
        return d;
    }

    public void setD(int d) {
        this.d = d;
    }

    @PropertyAnnotation2(stringValue = "getter_e")
    public Date getE() {
        return e;
    }

    @PropertyAnnotation2(stringValue = "getter_f")
    public List<String> getF() {
        return f;
    }

    @PropertyAnnotation1(stringValue = "getter_g")
    public short getG() {
        return g;
    }
}

@UseAnnotation(TypeAnnotation.class)
@UseAnnotation(DocumentedTypeAnnotation.class)
@UseAnnotation(InheritableTypeAnnotation.class)
@UseAnnotation({ FieldAnnotation1.class, FieldAnnotation2.class })
@UseAnnotation(MethodAnnotation1.class)
@UseAnnotation(PropertyAnnotation1.class)
public class InheritedAnnotationBeanViewConfigure {
}

@ViewOf(value = AnnotationBean.class, includePattern = ".*")
@UnUseAnnotation(FieldAnnotation2.class)
@UseAnnotation(value = PropertyAnnotation2.class, from = { AnnotationSource.CONFIG, AnnotationSource.TARGET_FIELD })
public class AnnotationBeanViewConfigure extends InheritedAnnotationBeanViewConfigure {

    @UseAnnotation(FieldAnnotation2.class)
    @OverrideViewProperty(AnnotationBeanMeta.c)
    private String[] c;

    // FieldAnnotation1 can not be put on a method, so this annotation is ignored.
    @UseAnnotation(value = FieldAnnotation1.class, dest = AnnotationDest.GETTER)
    // PropertyAnnotation1 is put on the field in the original class,
    // but will be put on getter method in the generated class.
    @UseAnnotation(value = PropertyAnnotation1.class, dest = AnnotationDest.GETTER)
    @OverrideViewProperty(AnnotationBeanMeta.d)
    private int d;

    @PropertyAnnotation2(stringValue = "config_e")
    @OverrideViewProperty(AnnotationBeanMeta.e)
    private Date e;

}
```
will generate
```java
@GeneratedView(targetClass = AnnotationBean.class, configClass = AnnotationBeanViewConfigure.class)
@InheritableTypeAnnotation(
    annotation = @ValueAnnotation1(type = {
        int.class
    }),
    annotations = {
        @ValueAnnotation1(type = {
            void.class
        }),
        @ValueAnnotation1(type = {
            String.class
        }),
        @ValueAnnotation1(type = {
            int[][][].class
        }),
        @ValueAnnotation1(type = {
            Void.class
        }),
        @ValueAnnotation1(type = {
            Void[].class
        })
    }
)
@TypeAnnotation(Date.class)
@DocumentedTypeAnnotation(
    enumValue = AEnum.B,
    enumValues = {
        AEnum.C,
        AEnum.B,
        AEnum.A
    }
)
public class AnnotationBeanView {

    @FieldAnnotation1(enumClassArray = { AEnum.class, AEnum.class })
    private Class<?> type;

    @FieldAnnotation1(doubleArray = {1.0})
    private String a;

    private String b;

    @FieldAnnotation1(charValue = '0')
    @FieldAnnotation2(stringArray = { "5" })
    private String[] c;

    private int d;

    @PropertyAnnotation2(stringValue = "config_e")
    private Date e;

    @PropertyAnnotation2(stringValue = "field_f")
    private List<String> f;

    @PropertyAnnotation1(stringValue = "field_g")
    private short g;

    // ignore other methods.

    public Class<?> getType() {
        return this.type;
    }

    public String getA() {
        return this.a;
    }

    public String getB() {
        return this.b;
    }

    public String[] getC() {
        return this.c;
    }

    @PropertyAnnotation1
    @MethodAnnotation1(
        charValue = 'a',
        annotations = {
            @ValueAnnotation1,
            @ValueAnnotation1(annotations = {
                @ValueAnnotation2
            })
        }
    )
    public int getD() {
        return this.d;
    }

    public Date getE() {
        return this.e;
    }

    public List<String> getF() {
        return this.f;
    }

    @PropertyAnnotation1(stringValue = "getter_g")
    public short getG() {
        return this.g;
    }

}
```
The full [example](/beanknife-examples/src/main/java/io/github/vipcxj/beanknife/cases/beans/AnnotationBeanViewConfigure.java).

#### change the generated class name
By default, the dto class is generated with the same package of the original class 
and the same name with a postfix 'View'. 
For example, the class a.b.c.Bean will generate a dto class a.b.c.BeanView.
With the same package, the DTO class can access the properties with protected or default access modifier.
However, if you really need to change the package or name of the generated class, you can configure like this:
```java
@ViewOf(value=a.b.c.Bean.class, genName="BeanDTO", packageName="a.b.c.dto")
public class ConfigureBean {}
``` 
Then the generated dto class will be a.b.c.dto.BeanDTO.

---
**NOTE**

If the packageName is set to different from the package of the original class, 
none of properties with protected or default modifier can be included in the generated DTO class.

---

#### filter the property
WIP

#### generate the setter method
```java
@ViewOf(setters=Access.PUBLIC)
public class ConfigureBean {}
```
By default, no setter methods is generated, it means `setters=Access.NONE`.
If only special property need setter method, 
`@ViewProperty`, `@NewViewProperty`, `@OverrideViewProperty` all have a attribute `setter`.
```java
public class OriginalBean {
    @ViewProperty(setter=Access.PUBLIC)
    private int a;
    private String b;
}

@ViewOf(OriginalBean.class)
public class ConfigureBean {
    @OverrideViewProperty(value="b", setter=Access.PUBLIC)
    private String b;
    @NewViewProperty(value="c", setter=Access.PUBLIC)
    public static boolean c() {
        return true;
    }
}
```

#### use non-static method to define new properties
By default, to define a new property in the configure bean, you need write a static method. 
As a result, the configure bean is stateless in the generated class.
Sometimes, you really need configure bean hold some state, you can use a non-static method to add the property.
Then the library runtime will scan the classpath to find a suitable implementation of service interface BeanProvider.
If it is found, the runtime will use the bean provider to get a configure bean instance and proxy the property method.
By default, there is no default bean provider. So an error will be thrown at runtime if you using a non-static method.
However, you can activate a default bean provider by following configure:
```java
@ViewOf(value=OriginalBean.class, useDefaultBeanProvider=true)
public class ConfigureBean {
    @NewViewProperty(value="c", setter=Access.PUBLIC)
    public boolean c() {
        return true;
    }
}
```
The default bean provider will use the empty constructor to instantiate the configure bean.
 So if there is no empty constructor or the empty constructor can't be accessed, a exception will be thrown at runtime.
 
 #### spring support
 In most cases, using non-static methods to configure new properties is not a good practice. 
 If you really need some state binding to the configure class, you can make the configure as a spring component.
 Then add the spring support dependency to the class path
 ```xml
<dependency>
    <groupId>io.github.vipcxj</groupId>
    <artifactId>beanknife-spring</artifactId>
    <version>${beanknife.version}</version>
</dependency>
```

This plugin provide a bean provider which lookup the bean in the spring application context. So with it, 
you can use a spring component to configure the DTO generation.

---
**NOTE**

To make it work, you need a spring boot environment.

---

#### serializable support
```java
@ViewOf(value=OriginalBean.class, serializable=true, serialVersionUID=12345L)
public class ConfigureBean {
    @OverrideViewProperty(value="b", setter=Access.PUBLIC)
    private String b;
    @NewViewProperty(value="c", setter=Access.PUBLIC)
    public static boolean c() {
        return true;
    }
}
```
By default, the generated class does not implement any interface. 
Set serializable to true make the generated class to implement the `Serializable` interface,
and add a static final long field `serialVersionUID` with 0L as initial value.
You can change its initial value by `ViewOf.serialVersionUID` attribute.

[maven-shield]: https://img.shields.io/maven-central/v/io.github.vipcxj/beanknife-core.png
[maven-link]: https://search.maven.org/artifact/io.github.vipcxj/beanknife-core