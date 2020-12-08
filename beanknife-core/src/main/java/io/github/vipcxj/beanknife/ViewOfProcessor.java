package io.github.vipcxj.beanknife;

import com.google.auto.service.AutoService;
import io.github.vipcxj.beanknife.models.Type;
import io.github.vipcxj.beanknife.models.ViewContext;
import io.github.vipcxj.beanknife.models.ViewOfData;
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

@SupportedAnnotationTypes("io.github.vipcxj.beanknife.annotations.ViewOf")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ViewOfProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                if (!element.getModifiers().contains(Modifier.ABSTRACT) && element.getKind() == ElementKind.CLASS) {
                    List<AnnotationMirror> annotationMirrors = Utils.extractAnnotations(
                            processingEnv,
                            element,
                            "io.github.vipcxj.beanknife.annotations.ViewOf",
                            "io.github.vipcxj.beanknife.annotations.ViewOfs"
                    );
                    TypeElement typeElement = (TypeElement) element;
                    Set<String> targetClassNames = new HashSet<>();
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        ViewOfData viewOf = ViewOfData.read(processingEnv, annotationMirror);
                        TypeElement targetElement = (TypeElement) viewOf.getValue().asElement();
                        if ("io.github.vipcxj.beanknife.utils.Self".equals(targetElement.getQualifiedName().toString())) {
                            targetElement = typeElement;
                        }
                        Type targetClassName = Type.extract(targetElement.asType());
                        Type genClassName = Utils.extractGenType(
                                targetClassName,
                                viewOf.getGenName(),
                                viewOf.getGenPackage(),
                                "View"
                        );
                        boolean samePackage = targetClassName.getPackageName().equals(genClassName.getPackageName());
                        String genQualifiedName = genClassName.getQualifiedName();
                        if (!targetClassNames.contains(genQualifiedName)) {
                            targetClassNames.add(genQualifiedName);
                            if (!Utils.canSeeFromOtherClass(typeElement, samePackage)) {
                                Utils.logError(
                                        processingEnv,
                                        "The target class "
                                                + targetClassName.getQualifiedName()
                                                + " can not be seen by the generated class "
                                                + genQualifiedName
                                                + "."
                                );
                                continue;
                            }
                            try {
                                writeBuilderFile(viewOf, targetElement, typeElement, genClassName);
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

    private void writeBuilderFile(ViewOfData viewOf, TypeElement baseElement, TypeElement configElement, Type genType) throws IOException {
        Modifier modifier = viewOf.getAccess();
        if (modifier == null) {
            return;
        }
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(genType.getFlatQualifiedName());
        try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
            Type baseType = Type.extract(baseElement.asType());
            ViewContext context = new ViewContext(processingEnv, baseType, genType, viewOf);
            context.collectData(baseElement, configElement);
            context.print(writer);
        }
    }
}
