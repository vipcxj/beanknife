import io.github.vipcxj.beanknife.core.spi.ViewCodeGenerator;
import io.github.vipcxj.beanknife.jpa.JpaViewCodeGenerator;

module beanknife.jpa {
    requires beanknife.core;
    requires beanknife.runtime;
    requires beanknife.jpa.runtime;
    requires org.apache.commons.text;
    requires java.compiler;
    provides ViewCodeGenerator with JpaViewCodeGenerator;
}