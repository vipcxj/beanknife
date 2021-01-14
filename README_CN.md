BeanKnife
===============

[![Maven Release][maven-shield]][maven-link]

[English Version](/README.md)

本项目是一个java注解处理器（annotation processor）。可以基于已有的类自动生成一个新的类。这里生成的是源码，而非字节码。生成的源码会被jdk当一般源码对待，进入正常的编译流程。

注解处理器是支持增量编译的。当jdk发现原类，原类的基类，配置类，配置类的基类源码有改动，将会重新生成生成类。

该项目最常见的用途是自动生成DTO（Data Transfer Object）。设想以下3个使用场景：
1. 有一个非常大的对象，内部属性繁多，根据业务需要向客户端返回这个对象的部分信息。
如果直接返回原对象，就会浪费很多带宽。这时更好的办法是建一个新类，仅保留其中需要的属性，即DTO。
这个任务虽然简单，但重复又繁琐，还维护困难。非常适合自动化。这正合适本项目大展身手。
2. 同样是希望序列化数据返回给客户端的场景。
即使需要的是原对象所有或几乎全部的信息，但若其中有循环引用，这种对象序列化起来就会很麻烦。
虽然各个主流JSON库都有一些配置来解决循环引用的问题，但往往效果都不是很好。
举个例子，类A有属性List<B> bList，类B有属性A a，这里可能产生两种业务需求。
   1. 以A为主，剔除bList中的B对象的a属性；
   2. 以B为主，剔除a的bList属性，或者保留a，但需要剔除a的bList中的B对象的a属性。

   这些个需求只能靠DTO，靠配置JSON是很难实现的。
3. 一个原始对象，需要被多个服务序列化，但每个服务需要的数据形状都有细微差别。
这就不得不为每个服务的序列化过程分别做定制，这种办法可扩展性差，代码可读性也差。
若是为每个服务分别定制DTO，既可以解耦，也提高了代码可读性。就是维护麻烦。
但使用本项目后，维护也不再是问题。

## 目录
* [使用需求](#使用需求)
* [快速上手](#快速上手)
* [基础知识](#基础知识)
* [配置注解](#配置注解)
* [场景用例](#场景用例)

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
施工中...

### 配置注解
施工中...

### 场景用例
施工中...

[maven-shield]: https://img.shields.io/maven-central/v/io.github.vipcxj/beanknife-core.png
[maven-link]: https://search.maven.org/artifact/io.github.vipcxj/beanknife-core