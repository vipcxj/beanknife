import io.github.vipcxj.beanknife.core.GeneratedMetaProcessor;
import io.github.vipcxj.beanknife.core.ViewMetaProcessor;
import io.github.vipcxj.beanknife.core.ViewOfProcessor;

import javax.annotation.processing.Processor;

module beanknife.core {
    uses io.github.vipcxj.beanknife.core.spi.ViewCodeGenerator;
    exports io.github.vipcxj.beanknife.core;
    exports io.github.vipcxj.beanknife.core.models;
    exports io.github.vipcxj.beanknife.core.spi;
    exports io.github.vipcxj.beanknife.core.utils;
    requires jdk.compiler;
    requires static com.github.spotbugs.annotations;
    requires beanknife.runtime;
    requires org.apache.commons.text;
    provides Processor with ViewMetaProcessor, ViewOfProcessor, GeneratedMetaProcessor;
}