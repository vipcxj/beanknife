package io.github.vipcxj.beanknife.tests;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import io.github.vipcxj.beanknife.ClassName;
import io.github.vipcxj.beanknife.ViewMetaProcessor;
import org.junit.Test;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.util.Arrays;

public class ViewMetaProcessorTest {

    @Test
    public void testBean1() {
        testViewMetaCase(
                "io.github.vipcxj.beanknife.tests.beans.TestBean1",
                new String[] {
                        null,
                        "io.github.vipcxj.beanknife.tests.beans.ViewOfTestBean1",
                        "io.github.vipcxj.beanknife.tests.otherbeans.TestBean1Meta",
                        "io.github.vipcxj.beanknife.tests.otherbeans.ViewOfTestBean1"
                }
        );
        testViewMetaCase("io.github.vipcxj.beanknife.tests.beans.TestBean1Child", null);
    }

    private String toSourcePath(String qualifiedClassName) {
        return qualifiedClassName.replaceAll("\\.", "/") + ".java";
    }

    private void testViewMetaCase(String qualifiedClassName, String[] targetQualifiedClassNames) {
        String sourcePath = toSourcePath(qualifiedClassName);
        Compilation compilation = Compiler.javac()
                .withProcessors(new ViewMetaProcessor())
                .compile(JavaFileObjects.forResource(sourcePath));
        if (targetQualifiedClassNames == null) {
            String genClassName = qualifiedClassName + "Meta";
            String genClassPath = toSourcePath(genClassName);
            CompilationSubject.assertThat(compilation)
                    .generatedSourceFile(genClassName)
                    .hasSourceEquivalentTo(JavaFileObjects.forResource(genClassPath));
        } else {
            for (String targetQualifiedClassName : targetQualifiedClassNames) {
                String genClassName = targetQualifiedClassName == null ? qualifiedClassName + "Meta" : targetQualifiedClassName;
                String genClassPath = toSourcePath(genClassName);
                CompilationSubject.assertThat(compilation)
                        .generatedSourceFile(genClassName)
                        .hasSourceEquivalentTo(JavaFileObjects.forResource(genClassPath));
            }
        }
    }

}
