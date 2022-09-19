package io.github.vipcxj.beanknife.core;

import com.sun.source.util.Trees;
import io.github.vipcxj.beanknife.core.models.MetaContext;
import io.github.vipcxj.beanknife.core.models.ViewMetaData;
import io.github.vipcxj.beanknife.core.models.ViewOfData;
import io.github.vipcxj.beanknife.core.utils.AnnotationUtils;
import io.github.vipcxj.beanknife.core.utils.Constants;
import io.github.vipcxj.beanknife.core.utils.JetbrainUtils;
import io.github.vipcxj.beanknife.core.utils.Utils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

@SupportedAnnotationTypes({"io.github.vipcxj.beanknife.runtime.annotations.ViewOf", "io.github.vipcxj.beanknife.runtime.annotations.ViewOfs"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ViewOfProcessor extends AbstractProcessor {

    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        System.out.println("ViewOfProcessor Init");
        super.init(processingEnv);
        ProcessingEnvironment unwrappedProcessingEnv = JetbrainUtils.jbUnwrap(ProcessingEnvironment.class, this.processingEnv);
        this.trees = Trees.instance(unwrappedProcessingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (TypeElement annotation : annotations) {
                Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
                for (Element element : elements) {
                    if (element.getKind() == ElementKind.CLASS) {
                        List<AnnotationMirror> annotationMirrors = Utils.getAnnotationsOn(processingEnv.getElementUtils(), element, Constants.VIEW_OF_TYPE_NAME, Constants.VIEW_OFS_TYPE_NAME, false, false);
                        TypeElement typeElement = (TypeElement) element;
                        Set<String> metaClassNames = new HashSet<>();
                        for (AnnotationMirror annotationMirror : annotationMirrors) {
                             ViewOfData viewOf = ViewOfData.read(processingEnv, annotationMirror, typeElement);
                            if (hasMeta(roundEnv, viewOf)) {
                                continue;
                            }
                            List<ViewOfData> viewOfDataList = Utils.collectViewOfs(processingEnv, roundEnv, viewOf.getTargetElement(), viewOf.getMetaPackage());
                            TypeElement mostImportantViewConfigElement = getMostImportantViewConfigElement(viewOfDataList);
                            if (Objects.equals(typeElement, mostImportantViewConfigElement)) {
                                ViewMetaData viewMetaData = new ViewMetaData(viewOf.getMetaName(), viewOf.getMetaPackage(), viewOf.getTargetElement(), viewOf.getConfigElement());
                                MetaContext metaContext = new MetaContext(trees, processingEnv, viewMetaData, viewOfDataList);
                                String metaClassName = metaContext.getGenType().getQualifiedName();
                                if (!metaClassNames.contains(metaClassName)) {
                                    metaClassNames.add(metaClassName);
                                    try {
                                        Utils.writeMetaFile(metaContext);
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
        return true;
    }

    private boolean hasMeta(RoundEnvironment roundEnv, ViewOfData viewOfData) {
        TypeElement targetElement = viewOfData.getTargetElement();
        String metaPackage = viewOfData.getMetaPackage();
        TypeElement viewMetaTypeElement = processingEnv.getElementUtils().getTypeElement(Constants.VIEW_META_TYPE_NAME);
        Set<? extends Element> candidates = roundEnv.getElementsAnnotatedWith(viewMetaTypeElement);
        for (Element candidate : candidates) {
            if (Utils.shouldIgnoredElement(candidate)) {
                continue;
            }
            List<? extends AnnotationMirror> annotationMirrors = processingEnv.getElementUtils().getAllAnnotationMirrors(candidate);
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (Utils.isThisAnnotation(annotationMirror, Constants.VIEW_META_TYPE_NAME)) {
                    ViewMetaData viewMetaData = ViewMetaData.read(processingEnv, annotationMirror, (TypeElement) candidate);
                    if (Utils.isViewMetaTargetTo(viewMetaData, targetElement, metaPackage)) {
                        return true;
                    }
                }
            }
        }
        TypeElement viewMetasTypeElement = processingEnv.getElementUtils().getTypeElement(Constants.VIEW_METAS_TYPE_NAME);
        candidates = roundEnv.getElementsAnnotatedWith(viewMetasTypeElement);
        for (Element candidate : candidates) {
            if (Utils.shouldIgnoredElement(candidate)) {
                continue;
            }
            List<? extends AnnotationMirror> annotationMirrors = processingEnv.getElementUtils().getAllAnnotationMirrors(candidate);
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (Utils.isThisAnnotation(annotationMirror, Constants.VIEW_METAS_TYPE_NAME)) {
                    Map<? extends ExecutableElement, ? extends AnnotationValue> elementValuesWithDefaults = processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);
                    List<AnnotationMirror> viewMetas = AnnotationUtils.getAnnotationElement(annotationMirror, elementValuesWithDefaults);
                    for (AnnotationMirror viewMeta : viewMetas) {
                        ViewMetaData viewMetaData = ViewMetaData.read(processingEnv, viewMeta, (TypeElement) candidate);
                        if (Utils.isViewMetaTargetTo(viewMetaData, targetElement, metaPackage)) {
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
