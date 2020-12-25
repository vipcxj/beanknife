package io.github.vipcxj.beanknife.tests;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;

import javax.annotation.processing.Processor;
import javax.tools.JavaFileObject;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {

    private static String toSourcePath(String qualifiedClassName) {
        return qualifiedClassName.replaceAll("\\.", "/") + ".java";
    }

    public static void testViewCase(List<Processor> processors, List<String> qualifiedClassNames, List<String> targetQualifiedClassNames) {
        List<JavaFileObject> sourceFiles = qualifiedClassNames.stream()
                .map(Utils::toSourcePath)
                .map(JavaFileObjects::forResource)
                .collect(Collectors.toList());
        Compilation compilation = Compiler.javac()
                .withProcessors(processors.toArray(new Processor[0]))
                .compile(sourceFiles);
        for (String targetQualifiedClassName : targetQualifiedClassNames) {
            String genClassPath = toSourcePath(targetQualifiedClassName);
            CompilationSubject.assertThat(compilation)
                    .generatedSourceFile(targetQualifiedClassName)
                    .hasSourceEquivalentTo(JavaFileObjects.forResource(genClassPath));
        }
    }
}
