package io.github.vipcxj.beanknife.runtime.annotations;

import io.github.vipcxj.beanknife.runtime.utils.BeanUsage;
import io.github.vipcxj.beanknife.runtime.utils.CacheType;
import io.github.vipcxj.beanknife.runtime.utils.Self;

import java.lang.annotation.*;

/**
 * Used to generate the DTO type.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
@Repeatable(ViewOfs.class)
public @interface ViewOf {
    /**
     * The targetType
     * @return the target type. By default the annotated class is used.
     */
    Class<?> value() default Self.class;

    /**
     * The configType
     * @return the config type. By default the annotated class is used.
     */
    Class<?> config() default Self.class;

    /**
     * The package name of the generated class. By default the package name of the target class is used.
     * @return the package name of the generated class
     */
    String genPackage() default "";
    /**
     * The simple name of the generated class. By default the simple name of the target class + View is used.
     * @return the simple name of the generated class
     */
    String genName() default "";

    /**
     * The access type of the generated class.
     * @return the access type of the generated class.
     */
    Access access() default Access.PUBLIC;

    /**
     * The included properties. By default, nothing is included.
     * @return the included properties
     */
    String[] includes() default {};
    /**
     * The excluded properties. By default, nothing is excluded.
     * @return the excluded properties
     */
    String[] excludes() default {};
    /**
     * The regex pattern of the included properties. By default, nothing is included.
     * @return the regex pattern of the included properties
     */
    String includePattern() default "";
    /**
     * The regex pattern of the excluded properties. By default, nothing is excluded.
     * @return the regex pattern of the excluded properties
     */
    String excludePattern() default "";

    /**
     * The access type of the empty constructor. By default, public is used.
     * @return the access type of the empty constructor
     */
    Access emptyConstructor() default Access.PUBLIC;
    /**
     * The access type of the field constructor. By default, public is used.
     * @return the access type of the field constructor
     */
    Access fieldsConstructor() default Access.PUBLIC;
    /**
     * The access type of the copy constructor. By default, public is used.
     * @return the access type of the copy constructor
     */
    Access copyConstructor() default Access.PUBLIC;
    /**
     * The access type of the read constructor. By default, public is used.
     * This constructor accept the original class instance as the only argument.
     * @return the access type of the read constructor
     */
    Access readConstructor() default Access.PUBLIC;
    /**
     * The access type of the getter methods. By default, public is used.
     * It can be override by the {@link ViewProperty}, {@link OverrideViewProperty} and {@link NewViewProperty}.
     * @return the access type of the getter methods
     */
    Access getters() default Access.PUBLIC;
    /**
     * The access type of the setter methods. By default, none is used, which means there are no setter method.
     * It can be override by the {@link ViewProperty}, {@link OverrideViewProperty} and {@link NewViewProperty}.
     * @return the access type of the setter methods
     */
    Access setters() default Access.NONE;

    /**
     * Used to control whether to add the error methods. Default is true.
     * @return whether to add the error methods
     */
    boolean errorMethods() default true;

    /**
     * Whether the generated class should implement {@link java.io.Serializable}. By default false.
     * @return whether the generated class should implement {@link java.io.Serializable}
     */
    boolean serializable() default false;

    /**
     * Specialize the serialVersionUID value of the {@link java.io.Serializable}. Only valid when {@link ViewOf#serializable()} is <code>true<code/>.
     * @return the serialVersionUID value of the {@link java.io.Serializable}
     */
    long serialVersionUID() default 0L;

    /**
     * When initialize the configure bean instance, whether to use the default bean provider.
     * The default bean provider use the empty constructor to initialize the bean.
     * @see io.github.vipcxj.beanknife.runtime.spi.BeanProvider
     * @return whether to use default bean provider
     */
    boolean useDefaultBeanProvider() default false;

    /**
     * The cache type of the configure bean instance achieved from {@link io.github.vipcxj.beanknife.runtime.spi.BeanProvider}.
     * By default, {@link CacheType#LOCAL} is used. it means the configure bean instance is cached as a private field in the generated class.
     * {@link CacheType#NONE} means no cache, call {@link io.github.vipcxj.beanknife.runtime.spi.BeanProvider#get(Class, BeanUsage, Object)} every time.
     * {@link CacheType#GLOBAL} means cached only once in the whole application context.
     * @return The cache type of the configure bean instance
     */
    CacheType configureBeanCacheType() default CacheType.LOCAL;

    /**
     * Generate a write-back method. Which writing back to the original type instance.
     * The method does not create a new original type instance.
     * You should create it yourself and send it to the method as a parameter.
     * This means collection and map version write-back methods are not possible.
     * @return The access level of the generated write-back method. By default, {@link Access#NONE} is used, it means no method is generated.
     */
    Access writeBackMethod() default Access.NONE;

    /**
     * Generate a create-and-write-back method. Which creating a new original type instance and then writing back to it.
     * The method create a new original type instance through {@link io.github.vipcxj.beanknife.runtime.spi.BeanProvider}.
     * By default, no default bean-provider is provided. Set {@link #useDefaultBeanProvider()} to true to activate the default bean provider which just new the instance by reflection.
     * You can also change the default behaviour by put the annotation {@link ViewUseDefaultBeanProvider} on a base configuration class.
     * Because there is no external parameters is used, The collection and map version create-and-write-back methods are generated as well.
     * @see #useDefaultBeanProvider()
     * @see ViewUseDefaultBeanProvider
     * @return The access level of the generated create-and-write-back method. By default, {@link Access#NONE} is used, it means no method is generated.
     */
    Access createAndWriteBackMethod() default Access.NONE;
}
