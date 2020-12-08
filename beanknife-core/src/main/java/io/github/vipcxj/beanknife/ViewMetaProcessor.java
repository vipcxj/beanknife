package io.github.vipcxj.beanknife;

import com.google.auto.service.AutoService;
import io.github.vipcxj.beanknife.models.MetaContext;
import io.github.vipcxj.beanknife.models.Type;
import io.github.vipcxj.beanknife.models.ViewMetaData;
import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes({"io.github.vipcxj.beanknife.annotations.ViewMeta", "io.github.vipcxj.beanknife.annotations.ViewMetas"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ViewMetaProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                if (!element.getModifiers().contains(Modifier.ABSTRACT) && element.getKind() == ElementKind.CLASS) {
                    List<AnnotationMirror> annotationMirrors = Utils.extractAnnotations(
                            processingEnv,
                            element,
                            "io.github.vipcxj.beanknife.annotations.ViewMeta",
                            "io.github.vipcxj.beanknife.annotations.ViewMetas"
                    );
                    TypeElement configElement = (TypeElement) element;
                    Set<String> targetClassNames = new HashSet<>();
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        ViewMetaData viewMeta = ViewMetaData.read(processingEnv, annotationMirror, configElement);
                        TypeElement sourceElement = viewMeta.getOf();
                        Type genType = Utils.extractGenType(
                                Type.extract(sourceElement.asType()),
                                viewMeta.getValue(),
                                viewMeta.getPackageName(),
                                "Meta"
                        );
                        String genQualifiedName = genType.getQualifiedName();
                        if (!targetClassNames.contains(genQualifiedName)) {
                            targetClassNames.add(genQualifiedName);
                            try {
                                writeBuilderFile(genType, sourceElement);
                            } catch (IOException e) {
                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                            }
                        } else {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Repeated ViewMeta annotation with class name: " + genQualifiedName + ".");
                        }
                    }
                } else {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "");
                }
            }
        }
        return true;
    }

    private void writeBuilderFile(Type genType, TypeElement element) throws IOException {
        String metaClassName = genType.getQualifiedName();
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(metaClassName);
        try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
            MetaContext context = new MetaContext(processingEnv, genType);
            context.collectData(element);
            context.print(writer);
        }
    }
}
