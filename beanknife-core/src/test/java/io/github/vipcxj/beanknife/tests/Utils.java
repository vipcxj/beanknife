package io.github.vipcxj.beanknife.tests;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

import javax.annotation.processing.Processor;
import java.util.List;

public class Utils {

    private static String toSourcePath(String qualifiedClassName) {
        return qualifiedClassName.replaceAll("\\.", "/") + ".java";
    }

    public static void testViewCase(List<Processor> processors, String qualifiedClassName, String[] targetQualifiedClassNames, String postfix) {
        String sourcePath = toSourcePath(qualifiedClassName);
        Compilation compilation = Compiler.javac()
                .withProcessors(processors.toArray(new Processor[0]))
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
