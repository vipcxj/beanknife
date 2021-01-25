BeanKnife
===============

[![Maven Release][maven-shield]][maven-link]

[English Version](/README.md)

本项目是一个java注解处理器（annotation processor）。可以基于已有的类自动生成一个新的类。这里生成的是源码，而非字节码。生成的源码会被jdk当一般源码对待，进入正常的编译流程。

注解处理器是支持增量编译的。当jdk发现原类，原类的基类，配置类，配置类的基类源码有改动，将会重新生成生成类。

该项目最常见的用途是自动生成DTO（Data Transfer Object）。设想以下4个使用场景：
1. 有一个非常大的对象，内部属性繁多，根据业务需要向客户端返回这个对象的部分信息。
如果直接返回原对象，就会浪费很多带宽。这时更好的办法是建一个新类，仅保留其中需要的属性，即DTO。
这个任务虽然简单，但重复又繁琐，还维护困难。非常适合自动化。这正合适本项目大展身手。
2. 同样是希望序列化数据返回给客户端的场景。
即使需要的是原对象所有或几乎全部的信息，但若其中有循环引用，这种对象序列化起来就会很麻烦。
虽然各个主流JSON库都有一些配置来解决循环引用的问题，但往往效果都不是很好。
举个例子，类A有属性List<B\> bList，类B有属性A a，这里可能产生两种业务需求。
   1. 以A为主，剔除bList中的B对象的a属性；
   2. 以B为主，剔除a的bList属性，或者保留a，但需要剔除a的bList中的B对象的a属性。

   这些个需求只能靠DTO，靠配置JSON是很难实现的。
3. 一个原始对象，需要被多个服务序列化，但每个服务需要的数据形状都有细微差别。
这就不得不为每个服务的序列化过程分别做定制，这种办法可扩展性差，代码可读性也差。
若是为每个服务分别定制DTO，既可以解耦，也提高了代码可读性。就是维护麻烦。
但使用本项目后，维护也不再是问题。
4. 如果使用了jpa，希望查询部分字段而非整个实体，这时就必须使用jpql或原生sql来指定查询特定字段。
而为了放置查询结果，就需要构建一个新类，即DTO。除了这个DTO的构建可以交给机器自动化外，
相关的查询语句中select部分也是可以交给机器自动化的，而本工具的jpa插件正好提供了这一功能。

## 目录
* [使用需求](#使用需求)
* [快速上手](#快速上手)
* [基础知识](#基础知识)
  * [基础属性](#基础属性)
  * [原类,生成类和配置类](#原类,生成类和配置类)
  * [ViewOf注解](#ViewOf注解)
  * [扩展属性](#扩展属性)
  * [配置继承](#配置继承)
* [配置注解](#配置注解)
* [场景用例](#场景用例)
* [JPA插件](#JPA插件)
* [自定义编译时插件](#自定义编译时插件)
* [Spring插件](#Spring插件)
* [自定义运行时插件](#自定义运行时插件)

### 使用需求
Jdk 1.8+ (包含 jdk 1.8)

### 快速上手
如果你使用maven，
先添加运行时依赖：
```xml
<dependencies>
  <dependency>
    <groupId>io.github.vipcxj</groupId>
    <artifactId>beanknife-runtime</artifactId>
    <version>${beanknife.version}</version>
  </dependency>
</dependencies>
```
然后是配置注解处理器：
```xml
<plugins>
  <plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
      <annotationProcessorPaths>
        <!-- 其他注解处理器，比如lombok -->
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
然后就可以开始使用了。下面是个最简单的例子：
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
假设已经存在一个类`SimpleBean`，你需要基于它生成一个新的类。新类有原类所有的属性。
你唯一需要做的就是为`SimpleBean`加上`ViewOf`注解，并使用`includePattern`将所有属性都包括进来。
于是在下一次编译发生前，jdk会自动生成一个新类`SimpleBeanView`的源文件。
`SimpleBeanView`是生成类默认的名字，它默认位于原类相同的包下。当然这一切都是可以配置的。
新类`SimpleBeanView`源码文件的位置根据构建工具的不同可能也不同，
若想要找到它，最简单的办法是在源码某处使用`SimpleBeanView`，然后使用IDE的定位功能，直接跳转到源码。
若你发现`SimpleBeanView`还不能使用，说明它还未被生成，手动编译一下即可。
BeanKnife是支持增量编译的，所以你没必要为了生成类，而特意去clean。
最终生成的`SimpleBeanView`大致长成下面那样：
```java
@GeneratedView(targetClass = SimpleBean.class, configClass = SimpleBean.class)
public class SimpleBeanView {

    private String a;

    private Integer b;

    private long c;
  
    // 空构造函数
    public SimpleBeanView() { }
   
    // 字段构造函数，JPQL语句中很有用
    public SimpleBeanView(
        String a,
        Integer b,
        long c
    ) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    // copy构造函数
    public SimpleBeanView(SimpleBeanView source) {
        this.a = source.a;
        this.b = source.b;
        this.c = source.c;
    }

    // reader构造函数，即接受原类，转化为新类
    public SimpleBeanView(SimpleBean source) {
        if (source == null) {
            throw new NullPointerException("The input source argument of the read constructor of class io.github.vipcxj.beanknife.cases.beans.SimpleBeanView should not be null.");
        }
        this.a = source.getA();
        this.b = source.getB();
        this.c = source.getC();
    }

    // read方法，将原类转为新类的静态方法
    public static SimpleBeanView read(SimpleBean source) {
        if (source == null) {
            return null;
        }
        return new SimpleBeanView(source);
    }

    // 数组版本的read方法
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

    // List版本的read方法
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

    // Set版本的read方法
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
    
    // Stack版本的read方法
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

    // Map版本的read方法
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

    // 以下是getter函数。默认只生成getter，而不生产setter，当然是可以通过配置来改变默认行为的
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
可以看到即使只有十来行的简单类，也能生成那么一大堆东西，以后随着功能的演进，可能还会增加更多东西。
全部自己手写，何其麻烦。

---
注意

在上面这个例子中，直接将`@ViewOf`放在了原类上，这仅仅是为了简单起见。
推荐使用配置类（下面会介绍）来进行配置，这样一方面不再有侵入性，
另一方面利用配置继承特性，可以实现几乎全局的默认配置修改。
比如BeanKnife默认不生成Setter函数，但一些人可能并不喜欢这样。
在基类上使用`@ViewSetter`注解，然后所有其他配置类都继承这个基类，就可以实现全局生成Setter函数。

---

### 基础知识
#### 基础属性
Beanknife的代码生成完全是围绕属性这一概念来的。
对于一个类A，可用属性的提取规则如下：
1. A的所有字段被认为是属性，属性名为字段名，称为字段属性
2. A的所有getter方法被认为是属性，属性名为getter方法根据Javabean规则对应的字段名（该字段不一定需要实际存在），
称为getter属性
3. 若存在同名属性，getter属性覆盖字段属性。
特殊的，若字段属性是可见的（比如是public的），但getter属性不可见（比如是private的），getter属性覆盖字段属性后，将导致该属性不可见。
4. 字段属性和getter属性合称基础属性，因为它们都基于原类。

下面举个例子进行讲解
```java
class Bean {
    private String a;  // (1)
    public int b;  // (2)
    protected long c;  // (3)
    public short d;  // (4)

    public String getA() { return this.a; } // (5)
    private short getD() { return this.d; } // (6)
}
```
1. 可见性为`private`的字段属性`a`
2. 可见性为`public`的字段属性`b`
3. 可见性为`protected`的字段属性`c`
4. 可见性为`public`的字段属性`d`
5. 可见性为`public`的getter属性`a`，将覆盖字段属性`a`
6. 可见性为`private`的getter属性`d`，将覆盖字段属性`d`

最终可用属性为`public`的getter属性`a`，`public`的字段属性`b`，
`protected`的字段属性`c`，`private`的getter属性`d`。

属性能否被生成类使用，还依赖于其可见性。可见性不是绝对的，而是相对的。就常理而言，属性在生成类中可见，才能被合法使用。
1. 生成类和原类同一个包，只要不是`private`的属性就是可见的。对于上面的例子，`a`,`b`,`c`都是可见的属性。
2. 生成类和原类不同包，只有`public`的属性才是可见的。对于上面的例子，`a`,`b`是可见的。

#### 原类,生成类和配置类
**Beanknife**生成新类必须使用一个已有类作为模板。这个已有类就称为**原类**。而生成的新类就称为**生成类**。
**Beanknife**基于注解来配置生成类的具体细节。用于放置注解的类就是配置类。可以在原类上直接进行配置，这时原类同时也是配置类；
也可以在第三方类上进行配置，这时这个第三方类就是配置类。推荐使用后者，这样一没有侵入性，二支持[扩展属性](#扩展属性)。

#### ViewOf注解
是否生成类，基于什么类来生成类，生成类的包名，类名，是由`@ViewOf`注解唯一决定的。
虽然还存在大量其他注解可以用于配置生成类，但都必须遵循一个大前提，那就是已经配置了`@ViewOf`注解。
`@ViewOf`最重要且不可代替的三个属性是`value`，`config`和`genPackage`。
1. `value`决定了基于什么类来生成新类。即[原类](#原类,生成类和配置类)。若不指定，则默认使用注解所在的当前类。
2. `config`决定了[配置类](#原类,生成类和配置类)的位置。没错，别怀疑，`@ViewOf`注解不一定要和配置类放一起。
当然若不指定，默认情况下`@ViewOf`注解所在的类就是[配置类](#原类,生成类和配置类)。
4. `genPackage`决定了[生成类](#原类,生成类和配置类)所在的包。默认为[原类](#原类,生成类和配置类)所在的包。
这样的好处是[基础属性](#基础属性)的可见性会更高，可以使用尽可能多的基础属性。

其他属性参见[配置注解](#配置注解)。

#### 扩展属性
除了[基础属性](#基础属性)，Beanknife还支持扩展属性。若要使用扩展属性，[配置类](#原类,生成类和配置类)不能等于[原类](#原类,生成类和配置类)，即必须使用第三方类作为配置类。理由看下去就能明白。

扩展属性按定义方式可分为
1. 扩展字段属性： 在配置类中使用字段定义扩展属性，属性名由相关注解决定，字段类型即属性类型。
2. 扩展方法属性： 在配置类中使用方法定义扩展属性，属性名由相关注解决定，方法返回类型型即属性类型。

扩展属性按作用方式可分为
1. 覆盖： 使用`@OverrideViewProperty`注解，覆盖同名[基础属性](#基础属性)。要求对应基础属性必须存在且在生成类中可见。
2. 映射： 使用`@MapViewProperty`注解，覆盖指定[基础属性](#基础属性)，并使用新的属性名。要求对应基础属性必须存在且在生成类中可见。
3. 新增： 使用`@NewViewProperty`注解，增加一个新的属性，属性名不能与已经存在且可见的[基础属性](#基础属性)冲突。

对于扩展方法属性又可分为
1. 静态： 方法上不存在`@Dynamic`注解。这里的静态意味着该属性在生成类中存在真实存在的对应字段，所定义的扩展方法仅仅用于为该字段赋初始值，不影响该字段后续读写操作。
2. 动态： 方法被`@Dynamic`注释。这里的动态意味着该属性在生成类中不存在对应字段，扩展方法将在属性对应的getter方法中被执行，用以实时获得属性值。

下面通过几个例子来具体说明扩展属性的各种类型
```java
@ViewOf(Bean.class)
class FieldDtoConfiguration {
    @OverrideViewProperty("a")
    @NullStringAsEmpty
    private String a; // (1)
   
    @MapViewProperty(name="newB", map="b")
    private int b; // (2)
    
    @NewViewProperty("f")
    private String f; // (3)
}
```
1. 字段a定义了一个覆盖扩展字段属性。它覆盖了原类中的基本属性a。
这里的覆盖也可以理解为修改。若想修改一个基本属性在生成类中的可见性和类型，就需要使用覆盖操作。
对于扩展字段属性，修改最终类型可以通过两个途径。
   1. 通过converter，本例中`@NullStringAsEmpty`相当于`@UsePropertyConverter(NullStringAsEmptyConverter.class)`。
   [NullStringAsEmptyConverter](/beanknife-runtime/src/main/java/io/github/vipcxj/beanknife/runtime/converters/NullStringAsEmptyConverter.java)是一个[PropertyConverter](/beanknife-runtime/src/main/java/io/github/vipcxj/beanknife/runtime/PropertyConverter.java),
   具有[convert](/beanknife-runtime/src/main/java/io/github/vipcxj/beanknife/runtime/PropertyConverter.java#L20)方法，可用于将对象由`String`转为`String`。这里具体作用是若为null则转为空字符串，否则不变。
   2. 若指定的类型恰好是原类对应基础字段类型的生成类或生产类的同构体，并且转换方法不需要额外参数，则该转换能自动完成。
   举个例子：若存在基础属性a，类型为A，并存在以A为原类的生成类`ADto`，则可以定义一个类型为`ADto`的扩展字段属性覆盖a，两者间的类型转换将由工具自动完成。
   更进一步，若a的类型为`List<A>`,则对应的生成类同构体为`List<ADto>`，若a的类型为`Map<String, A[]>[]`，则对应的生成类同构体为`Map<String, ADto[]>[]`。`List`， `Set`， 和`Array`都是支持的。
   
2. 字段`b`覆盖隐藏了基础属性`b`，并定义了一个新的映射扩展字段属性，属性名为`newB`。映射扩展字段属性本质上只是覆盖扩展字段属性加改个名。所以覆盖扩展字段属性能做到的，它也能做到。
3. 字段`f`定义了一个新增扩展字段属性。它在原类中没有对应的基础属性，所以在初始化时，需要额外为其传入一个初始值。

```java
@ViewOf(Bean.class)
class MethodDtoConfiguration {
    // (1)
    @OverrideViewProperty("a")
    public static String a(@InjectProperty("a") String a) {
        return a != null ? a : "";
    }
    
    // (2)
    @MapViewProperty(name="newB", map="b")
    @Dynamic
    public static String b(@InjectProperty("b") String a) {
        return "new" + a;
    }
    
    // (3)
    @NewViewProperty("now")
    public static Date now() {
        return new Date();
    }
    
    // (4)
    @NewViewProperty("f")
    @Dynamic
    public static String f(@InjectProperty("newB") String newB, @InjectProperty("now") Date now) {
        return newB + now;
    }
}
```
1. 定义了静态覆盖扩展方法属性`a`，实际效果和上例中定义的覆盖扩展字段属性`a`一样，都是将基础属性`a`在其为空的情况下转为空字符串。
这里的`@InjectProperty`用于注入原类的属性。除了注入单个属性，还能注入整个原类实例`String a(Bean source)`，或添加额外参数`String a(Bean source, @ExtraParam("extraParam") String extraParam)`。
静态方法属性只会在初始化时起作用，所以只能接受原类或原类的属性作为参数。
对于静态方法属性，参数必须满足下来三种情况之一，顺序并不重要。
   1. 参数类型为原类，用于注入原类实例，这种情况的参数有且只能有一个。
   2. 参数上有`@InjectProperty`注解，用于注入原类的某个可见的基础属性。可以有任意多个。
   3. 参数上有`@ExtraParam`注解，声明该参数是额外参数，在转换原类时必须额外传入。注意，一旦存在额外参数，上文覆盖字段属性中提到的原类到生成类的自动转换将不可用。 
   
   下面举几个例子
   
   i. 对于`String a(@InjectProperty("a") String a)`，生成类中将生成如下代码
   ```java
   public BeanView(Bean source) {
       this.a = MethodDtoConfiguration.a(source.getA());
       // other fields initialization
   }
   ```
   ii. 对于`String a(Bean source)`，生成类中将生成如下代码
    ```java
   public BeanView(Bean source) {
       this.a = MethodDtoConfiguration.a(source);
       // other fields initialization
   }
    ```
   iii. 对于`String a(Bean source, @ExtraParam("extraParam") String param)`，生成类中将生成如下代码
   ```java
   public BeanView(Bean source, String extraParam) {
       this.a = MethodDtoConfiguration.a(source, extraParam);
       // other fields initialization
   }
    ```
   
2. 定义了动态映射扩展方法属性`newB`，覆盖了基础属性`b`。
不同于静态扩展方法属性，动态扩展方法属性在生成类中不存在对应的字段，它将在对应属性的getter方法中被实时地执行。
所以不同于静态扩展方法属性，动态扩展方法属性不支持注入原类实例，但可以注入生成类实例，也就是`this`对象。另一方面额外参数也是不支持的，因为getter方法是无参的。
对于动态方法属性，参数必须满足下来两种情况之一，顺序并不重要。
   1. 参数上有`@InjectSelf`注解，注入生成类实例，也就是`this`对象。当然参数类型也必须正确。
   2. 参数上有`@InjectProperty`注解，注入生成类的指定属性，参数类型必须等于属性类型或是其基类。
   
   下面举几个例子，假设属性名都是a
   
   i. 对于`String a(@InjectProperty("a") String a)`，生成类中将生成如下代码
   ```java
   public String getA() {
       return MethodDtoConfiguration.a(this.a);
   }
   ```
   ii. 对于`String a(BeanView source)`，生成类中将生成如下代码
    ```java
   public String getA() {
       return MethodDtoConfiguration.a(this);
   }
    ```
   iii. 对于`String a(@InjectProperty("newB") String newB, @InjectProperty("now") Date now)`，生成类中将生成如下代码
   ```java
   public String getA() {
       return MethodDtoConfiguration.a(this.getNewB(), this.now);
   }
    ```
   
3. 定义了静态新增扩展方法属性now，生成如下代码
   ```java
   public BeanView(Bean source) {
       // other fields initialization
       this.now = MethodDtoConfiguration.now();
       // other fields initialization
   }
   ```

4. 定义了动态新增扩展方法属性f，生成如下代码
   ```java
   public String getF() {
       return MethodDtoConfiguration.f(this.getNewB(), this.now);
   }
   ```
   注意这里的`newB`属性是在2中定义的，`now`属性是在3中定义的，都是原类说没有的。
对于`newB`属性，因为它是动态方法属性，所以不存在对应字段，所以生成代码中使用了getter方法获取，而非字段获取。

#### 配置继承
Beanknife最大的目的就是为了偷懒，所以简化配置也是重点之一。于是配置继承就成了不可或缺的功能。
Beankinfe的配置类支持继承机制。这也是推荐使用配置类而不是直接在原类上配置的原因之一。
注解的继承性是Java的原生语言特性，Beanknife的配置继承就利用了这一特性。
所以判断一个注解是否支持继承，只要看它是否被`@Inherited`所注解。
因为java只支持类级别的注解继承。所以也只有用于类上的配置注解可以被继承。
事实上除了`@ViewOf`，几乎所有的类级别的配置注解都支持继承。而对于`@ViewOf`，它的绝大多数属性，都有对应的独立注解存在。而这些注解都是支持继承的。
这意味着Beanknife几乎所有配置都支持继承。

Beanknfie并不存在显式的全局配置机制。但这一功能可以通过设置一个公共基类，而其他所有配置类都继承这个基类实现。

因为Beanknife的类级配置几乎都围绕`@ViewOf`展开，所以这里列出`@ViewOf`的各属性，对应的可继承独立注解，以及继承方式。

| 属性                    | 独立注解                         | 合并方式        | 子类上的值       | 基类上的值        | 最终值                       |
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

为了更好的说明配置继承机制，这里举几个例子
```java
@ViewSerializable(true)
@ViewReadConstructor(Access.NONE)
@ViewSetters(Access.PROTECTED)
@ViewGenNameMapper("${name}Dto")
@ViewPropertiesExclude("a")
public class GrandparentViewConfigure {
}

@ViewGenNameMapper("ViewOf${name}")
@ViewPropertiesIncludePattern(".*")
public class Parent1ViewConfigure extends GrandparentViewConfigure {
}

@ViewOf(Leaf11Bean.class)
public class Leaf11BeanViewConfigure extends Parent1ViewConfigure {
}

@ViewOf(Leaf12Bean.class)
public class Leaf12BeanViewConfigure extends Parent1ViewConfigure {
}

@ViewSetters(Access.NONE)
// Not work, because "a" is excluded by parent configuration.
@ViewPropertiesInclude("a")
@ViewPropertiesExclude("b")
public class Parent2ViewConfigure extends GrandparentViewConfigure {
}

@ViewOf(Leaf21Bean.class)
@ViewPropertiesInclude(Leaf21BeanMeta.c)
public class Leaf21BeanViewConfigure extends Parent2ViewConfigure {
}
```
```
GrandparentViewConfigure
└───Parent1ViewConfigure
│       Leaf11BeanViewConfigure
│       Leaf12BeanViewConfigure
└───Parent2ViewConfigure
        Leaf21BeanViewConfigure
```
[GrandparentViewConfigure]: /beanknife-examples/src/main/java/io/github/vipcxj/beanknife/cases/beans/GrandparentViewConfigure.java
[Parent1ViewConfigure]: /beanknife-examples/src/main/java/io/github/vipcxj/beanknife/cases/beans/Parent1ViewConfigure.java
[Leaf11BeanViewConfigure]: /beanknife-examples/src/main/java/io/github/vipcxj/beanknife/cases/beans/Leaf11BeanViewConfigure.java
[Leaf12BeanViewConfigure]: /beanknife-examples/src/main/java/io/github/vipcxj/beanknife/cases/beans/Leaf12BeanViewConfigure.java
[Parent2ViewConfigure]: /beanknife-examples/src/main/java/io/github/vipcxj/beanknife/cases/beans/Parent2ViewConfigure.java
[Leaf21BeanViewConfigure]: /beanknife-examples/src/main/java/io/github/vipcxj/beanknife/cases/beans/Leaf21BeanViewConfigure.java

[Parent1ViewConfigure]继承了[GrandparentViewConfigure]的配置，根据上表，得到了如下的等价配置
```java
@ViewSerializable(true)
@ViewReadConstructor(Access.NONE)
@ViewSetters(Access.PROTECTED)
@ViewPropertiesExclude("a")
@ViewGenNameMapper("ViewOf${name}")
@ViewPropertiesIncludePattern(".*")
class Parent1ViewConfigure {
}
```
而[Leaf11BeanViewConfigure]和[Leaf12BeanViewConfigure]各自都继承了[Parent1ViewConfigure]。
不同于[Parent1ViewConfigure]和[GrandparentViewConfigure]，
[Leaf11BeanViewConfigure]和[Leaf12BeanViewConfigure]都使用了`@ViewOf`注解，这意味着只有它们俩才会真正激活以上那些配置，生成新的类。
做个类比，[Parent1ViewConfigure]和[GrandparentViewConfigure]相当于java中的接口，并不真正起作用，
只有当，[Leaf11BeanViewConfigure]和[Leaf12BeanViewConfigure]则相当于java中的非抽象类，实现了接口。

[Parent2ViewConfigure]也继承了[GrandparentViewConfigure]的配置，根据上表，得到了如下的等价配置
```java
@ViewSerializable(true)
@ViewReadConstructor(Access.NONE)
@ViewGenNameMapper("${name}Dto")
@ViewSetters(Access.NONE)
@ViewPropertiesInclude("a")
@ViewPropertiesExclude({"a", "b"})
public class Parent2ViewConfigure {
}
```
而[Leaf21BeanViewConfigure]继承了[Parent2ViewConfigure]并真正产生新代码。不同于上例，[Leaf21BeanViewConfigure]本身也带有配置并覆盖了上级配置，最终等价于
```java
@ViewOf(Leaf21Bean.class)
@ViewSerializable(true)
@ViewReadConstructor(Access.NONE)
@ViewGenNameMapper("${name}Dto")
@ViewSetters(Access.NONE)
@ViewPropertiesExclude({"a", "b"})
@ViewPropertiesInclude({"a", Leaf21BeanMeta.c})
public class Leaf21BeanViewConfigure {
}
```

### 配置注解
施工中...

### 场景用例
施工中...

### JPA插件
施工中...

### 自定义编译时插件
施工中...

### Spring插件
施工中...

### 自定义运行时插件
施工中...

[maven-shield]: https://img.shields.io/maven-central/v/io.github.vipcxj/beanknife-core.png
[maven-link]: https://search.maven.org/artifact/io.github.vipcxj/beanknife-core