package io.github.vipcxj.beanknife.tests;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import io.github.vipcxj.beanknife.ViewMetaProcessor;
import io.github.vipcxj.beanknife.ViewOfProcessor;
import org.junit.Test;

import javax.annotation.processing.Processor;

public class ViewMetaProcessorTest {

    @Test
    public void testBean1() {
        testViewCase(
                new ViewMetaProcessor(),
                "io.github.vipcxj.beanknife.tests.beans.TestBean1",
                new String[] {
                        null,
                        "io.github.vipcxj.beanknife.tests.beans.ViewOfTestBean1",
                        "io.github.vipcxj.beanknife.tests.otherbeans.TestBean1Meta",
                        "io.github.vipcxj.beanknife.tests.otherbeans.ViewOfTestBean1",
                        "io.github.vipcxj.beanknife.tests.beans.TestBean1$NestTestBean1Meta"
                },
                "Meta"
        );
        testViewCase(
                new ViewMetaProcessor(),
                "io.github.vipcxj.beanknife.tests.beans.TestBean1Child",
                null,
                "Meta"
        );
        testViewCase(
                new ViewMetaProcessor(),
                "io.github.vipcxj.beanknife.tests.beans.TestBean2",
                new String[] {
                        null,
                        "io.github.vipcxj.beanknife.tests.beans.ViewOfTestBean2",
                        "io.github.vipcxj.beanknife.tests.otherbeans.TestBean2Meta",
                        "io.github.vipcxj.beanknife.tests.otherbeans.ViewOfTestBean2",
                        "io.github.vipcxj.beanknife.tests.beans.TestBean1$NestTestBean2Meta"
                },
                "Meta"
        );
        testViewCase(
                new ViewOfProcessor(),
                "io.github.vipcxj.beanknife.tests.beans.GenericTestBean1",
                null,
                "View"
        );
    }

    private String toSourcePath(String qualifiedClassName) {
        return qualifiedClassName.replaceAll("\\.", "/") + ".java";
    }

    private void testViewCase(Processor processor, String qualifiedClassName, String[] targetQualifiedClassNames, String postfix) {
        String sourcePath = toSourcePath(qualifiedClassName);
        Compilation compilation = Compiler.javac()
                .withProcessors(processor)
                .compile(JavaFileObjects.forResource(sourcePath));
        if (targetQualifiedClassNames == null) {
            String genClassName = qualifiedClassName + postfix;
            String genClassPath = toSourcePath(genClassName);
            CompilationSubject.assertThat(compilation)
                    .generatedSourceFile(genClassName)
                    .hasSourceEquivalentTo(JavaFileObjects.forResource(genClassPath));
        } else {
            for (String targetQualifiedClassName : targetQualifiedClassNames) {
                String genClassName = targetQualifiedClassName == null ? qualifiedClassName + postfix : targetQualifiedClassName;
                String genClassPath = toSourcePath(genClassName);
                CompilationSubject.assertThat(compilation)
                        .generatedSourceFile(genClassName)
                        .hasSourceEquivalentTo(JavaFileObjects.forResource(genClassPath));
            }
        }
    }

}
