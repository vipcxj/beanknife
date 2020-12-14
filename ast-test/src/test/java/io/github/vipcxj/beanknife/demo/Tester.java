package io.github.vipcxj.beanknife.demo;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

public class Tester {

    @Test
    public void test() {
        Compilation compilation = Compiler.javac()
                .withProcessors(new TestProcessor())
                .compile(JavaFileObjects.forResource("io.github.vipcxj.beanknife.demo.WrongClass".replaceAll("\\.", "/") + ".java"));
        CompilationSubject.assertThat(compilation).failed();
    }
}
