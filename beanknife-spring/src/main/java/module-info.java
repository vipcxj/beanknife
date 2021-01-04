open module beanknife.spring {
    requires beanknife.runtime;
    requires spring.context;
    requires static com.github.spotbugs.annotations;
    provides io.github.vipcxj.beanknife.runtime.spi.BeanProvider with io.github.vipcxj.beanknife.spring.SpringBeanProvider;
}