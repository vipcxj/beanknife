package io.github.vipcxj.beanknife.asttest;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

public class Tester {

    @Test
    public void test() {
        JavaFileObject fileObject = JavaFileObjects.forResource("io.github.vipcxj.beanknife.asttest.WrongClass".replaceAll("\\.", "/") + ".java");
        Compilation compilation = Compiler.javac()
                .withProcessors(new TestProcessor())
                .compile(fileObject);
        CompilationSubject.assertThat(compilation).generatedSourceFile("");
    }
}
