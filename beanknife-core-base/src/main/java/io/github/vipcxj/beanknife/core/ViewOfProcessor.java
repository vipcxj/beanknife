package io.github.vipcxj.beanknife.core;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import io.github.vipcxj.beanknife.core.models.MetaContext;
import io.github.vipcxj.beanknife.core.models.ViewMetaData;
import io.github.vipcxj.beanknife.core.models.ViewOfData;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.runtime.annotations.ViewMeta;
import io.github.vipcxj.beanknife.runtime.annotations.ViewMetas;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOfs;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@SupportedAnnotationTypes({"io.github.vipcxj.beanknife.runtime.annotations.ViewOf", "io.github.vipcxj.beanknife.runtime.annotations.ViewOfs"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ViewOfProcessor extends AbstractProcessor {

    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = Trees.instance(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (TypeElement annotation : annotations) {
                Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
                for (Element element : elements) {
                    if (element.getKind() == ElementKind.CLASS) {
                        List<AnnotationMirror> annotationMirrors = Utils.getAnnotationsOn(processingEnv.getElementUtils(), element, ViewOf.class, ViewOfs.class, false, false);
                        TypeElement typeElement = (TypeElement) element;
                        Set<String> metaClassNames = new HashSet<>();
                        for (AnnotationMirror annotationMirror : annotationMirrors) {
                            ViewOfData viewOf = ViewOfData.read(processingEnv, annotationMirror, typeElement);
                            if (hasMeta(roundEnv, viewOf.getTargetElement())) {
                                continue;
                            }
                            List<ViewOfData> viewOfDataList = Utils.collectViewOfs(processingEnv, roundEnv, viewOf.getTargetElement());
                            TypeElement mostImportantViewConfigElement = getMostImportantViewConfigElement(viewOfDataList);
                            if (Objects.equals(typeElement, mostImportantViewConfigElement)) {
                                ViewMetaData viewMetaData = new ViewMetaData("", "", viewOf.getTargetElement(), viewOf.getTargetElement());
                                MetaContext metaContext = new MetaContext(trees, processingEnv, viewMetaData, viewOfDataList);
                                String metaClassName = metaContext.getGenType().getQualifiedName();
                                if (!metaClassNames.contains(metaClassName)) {
                                    metaClassNames.add(metaClassName);
                                    try {
                                        List<TypeElement> dependencies = Utils.calcDependencies(viewOf.getTargetElement());
                                        dependencies.add(typeElement);
                                        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(metaClassName, dependencies.toArray(new Element[0]));
                                        try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
                                            metaContext.collectData();
                                            metaContext.print(writer);
                                        }
                                    } catch (IOException e) {
                                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                                    }
                                }
                            }
                        }
                    } else {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "");
                    }
                }
            }
        } catch (Throwable t) {
            Utils.logError(processingEnv, t);
        }
        return false;
    }

    private boolean hasMeta(RoundEnvironment roundEnv, TypeElement targetElement) {
        Set<? extends Element> candidates = roundEnv.getElementsAnnotatedWith(ViewMeta.class);
        for (Element candidate : candidates) {
            if (Utils.shouldIgnoredElement(candidate)) {
                continue;
            }
            List<? extends AnnotationMirror> annotationMirrors = processingEnv.getElementUtils().getAllAnnotationMirrors(candidate);
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (Utils.isThisAnnotation(annotationMirror, ViewMeta.class)) {
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
                if (Utils.isThisAnnotation(annotationMirror, ViewMetas.class)) {
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

    private TypeElement getMostImportantViewConfigElement(List<ViewOfData> viewOfDataList) {
        return viewOfDataList.stream().map(ViewOfData::getConfigElement).max(Comparator.comparing(e -> e.getQualifiedName().toString())).orElse(null);
    }
}
