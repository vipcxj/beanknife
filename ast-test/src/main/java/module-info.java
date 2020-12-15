import io.github.vipcxj.beanknife.asttest.TestProcessor;

import javax.annotation.processing.Processor;

module beanknife.asttest {
    exports io.github.vipcxj.beanknife.asttest;
    requires com.google.auto.service;
    requires jdk.compiler;
    provides Processor with TestProcessor;
}