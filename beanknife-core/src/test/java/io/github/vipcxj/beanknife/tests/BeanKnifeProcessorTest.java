package io.github.vipcxj.beanknife.tests;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import io.github.vipcxj.beanknife.BeanKnifeProcessor;
import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class BeanKnifeProcessorTest {

    @Test
    public void testBean1() {
        Compilation compilation = Compiler.javac()
                .withProcessors(new BeanKnifeProcessor())
                .compile(JavaFileObjects.forResource("io/github/vipcxj/beanknife/tests/beans/TestBean1.java"));
        CompilationSubject.assertThat(compilation).succeeded();
        for (Diagnostic<? extends JavaFileObject> diagnostic : compilation.diagnostics()) {
            System.out.println(diagnostic.toString());
        }
    }
}
