package io.github.vipcxj.beanknife;

import com.google.auto.service.AutoService;
import io.github.vipcxj.beanknife.annotations.ViewMeta;
import io.github.vipcxj.beanknife.annotations.ViewMetas;
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
import java.util.Map;
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
                if (element.getKind() == ElementKind.CLASS) {
                    List<AnnotationMirror> annotationMirrors = Utils.extractAnnotations(
                            processingEnv,
                            element,
                            "io.github.vipcxj.beanknife.annotations.ViewOf",
                            "io.github.vipcxj.beanknife.annotations.ViewOfs"
                    );
                    TypeElement typeElement = (TypeElement) element;
                    Set<String> targetClassNames = new HashSet<>();
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        ViewOfData viewOf = ViewOfData.read(processingEnv, annotationMirror, typeElement);
                        ViewContext context = new ViewContext(processingEnv, viewOf);
                        if (shouldIgnore(roundEnv, context.getViewOf().getTargetElement())) {
                            continue;
                        }
                        String genQualifiedName = context.getGenType().getQualifiedName();
                        if (!targetClassNames.contains(genQualifiedName)) {
                            targetClassNames.add(genQualifiedName);
                            if (!Utils.canSeeFromOtherClass(typeElement, context.isSamePackage())) {
                                Utils.logError(
                                        processingEnv,
                                        "The target class "
                                                + context.getTargetType().getQualifiedName()
                                                + " can not be seen by the generated class "
                                                + genQualifiedName
                                                + "."
                                );
                                continue;
                            }
                            try {
                                writeBuilderFile(context);
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

    private void writeBuilderFile(ViewContext context) throws IOException {
        Modifier modifier = context.getViewOf().getAccess();
        if (modifier == null) {
            return;
        }
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(context.getGenType().getQualifiedName(), context.getViewOf().getTargetElement(), context.getViewOf().getConfigElement());
        try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
            context.collectData();
            context.print(writer);
        }
    }


    private boolean shouldIgnore(RoundEnvironment roundEnv, TypeElement targetElement) {
        Set<? extends Element> candidates = roundEnv.getElementsAnnotatedWith(ViewMeta.class);
        for (Element candidate : candidates) {
            if (Utils.shouldIgnoredElement(candidate)) {
                continue;
            }
            List<? extends AnnotationMirror> annotationMirrors = processingEnv.getElementUtils().getAllAnnotationMirrors(candidate);
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (((TypeElement) annotationMirror.getAnnotationType().asElement()).getQualifiedName().toString().equals(ViewMeta.class.getCanonicalName())) {
                    if (Utils.isViewMetaTargetTo(processingEnv, annotationMirror, (TypeElement) candidate, targetElement)) {
                        return true;
                    }
                }
            }
        }
        candidates = roundEnv.getElementsAnnotatedWith(ViewMetas.class);
        for (Element candidate : candidates) {
            if (Utils.shouldIgnoredElement(candidate)) {
                continue;
            }
            List<? extends AnnotationMirror> annotationMirrors = processingEnv.getElementUtils().getAllAnnotationMirrors(candidate);
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (((TypeElement) annotationMirror.getAnnotationType().asElement()).getQualifiedName().toString().equals(ViewMetas.class.getCanonicalName())) {
                    Map<? extends ExecutableElement, ? extends AnnotationValue> elementValuesWithDefaults = processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);
                    List<AnnotationMirror> viewMetas = Utils.getAnnotationElement(annotationMirror, elementValuesWithDefaults);
                    for (AnnotationMirror viewMeta : viewMetas) {
                        if (Utils.isViewMetaTargetTo(processingEnv, viewMeta, (TypeElement) candidate, targetElement)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
