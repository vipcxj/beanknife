package io.github.vipcxj.beanknife.core;

import com.sun.source.util.Trees;
import io.github.vipcxj.beanknife.core.models.MetaContext;
import io.github.vipcxj.beanknife.core.models.ViewMetaData;
import io.github.vipcxj.beanknife.core.models.ViewOfData;
import io.github.vipcxj.beanknife.core.utils.Constants;
import io.github.vipcxj.beanknife.core.utils.JetbrainUtils;
import io.github.vipcxj.beanknife.core.utils.Utils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@SupportedAnnotationTypes({"io.github.vipcxj.beanknife.runtime.annotations.ViewMeta", "io.github.vipcxj.beanknife.runtime.annotations.ViewMetas"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ViewMetaProcessor extends AbstractProcessor {

    private Trees trees;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
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
                        List<AnnotationMirror> annotationMirrors = Utils.getAnnotationsOn(processingEnv.getElementUtils(), element, Constants.VIEW_META_TYPE_NAME, Constants.VIEW_METAS_TYPE_NAME, false, false);
                        TypeElement configElement = (TypeElement) element;
                        Set<String> targetClassNames = new HashSet<>();
                        for (AnnotationMirror annotationMirror : annotationMirrors) {
                            ViewMetaData viewMeta = ViewMetaData.read(processingEnv, annotationMirror, configElement);
                            TypeElement targetElement = viewMeta.getOf();
                            TypeElement mostImportantViewMetaElement = getMostImportantViewMetaElement(roundEnv, targetElement);
                            if (mostImportantViewMetaElement != null && !Objects.equals(configElement, mostImportantViewMetaElement)) {
                                String genTypeName = Utils.extractGenTypeName(targetElement, viewMeta.getValue(), viewMeta.getPackageName(), "Meta");
                                Utils.logWarn(
                                        processingEnv,
                                        "The meta class \"" +
                                                genTypeName +
                                                "\" which configured on \"" +
                                                configElement.getQualifiedName() +
                                                "\" will not be generated, " +
                                                "because the class \"" +
                                                mostImportantViewMetaElement.getQualifiedName() +
                                                "\" has configured a similar meta class and has a higher priority.");
                                continue;
                            }
                            List<ViewOfData> viewOfDataList = Utils.collectViewOfs(processingEnv, roundEnv, targetElement);
                            MetaContext context = new MetaContext(trees, processingEnv, viewMeta, viewOfDataList);
                            String genQualifiedName = context.getGenType().getQualifiedName();
                            if (!targetClassNames.contains(genQualifiedName)) {
                                targetClassNames.add(genQualifiedName);
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
        } catch (Throwable throwable) {
            Utils.logError(processingEnv, throwable);
            return false;
        }
    }

    private void writeBuilderFile(MetaContext context) throws IOException {
        String metaClassName = context.getGenType().getQualifiedName();
        TypeElement[] dependencies = Utils.calcDependencies(context.getViewMeta().getOf()).toArray(new TypeElement[0]);
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(metaClassName, dependencies);
        try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
            context.collectData();
            context.print(writer);
        }
    }

    private TypeElement getMostImportantViewMetaElement(RoundEnvironment roundEnv, TypeElement targetElement) {
        TypeElement viewMetaTypeElement = processingEnv.getElementUtils().getTypeElement(Constants.VIEW_META_TYPE_NAME);
        Set<? extends Element> candidates = roundEnv.getElementsAnnotatedWith(viewMetaTypeElement);
        List<TypeElement> out = new ArrayList<>();
        for (Element candidate : candidates) {
            if (Utils.shouldIgnoredElement(candidate)) {
                continue;
            }
            List<? extends AnnotationMirror> annotationMirrors = processingEnv.getElementUtils().getAllAnnotationMirrors(candidate);
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (Utils.isThisAnnotation(annotationMirror, Constants.VIEW_META_TYPE_NAME)) {
                    if (Utils.isViewMetaTargetTo(processingEnv, annotationMirror, (TypeElement) candidate, targetElement)) {
                        out.add((TypeElement) candidate);
                        break;
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
                    List<AnnotationMirror> viewMetas = Utils.getAnnotationElement(annotationMirror, elementValuesWithDefaults);
                    for (AnnotationMirror viewMeta : viewMetas) {
                        if (Utils.isViewMetaTargetTo(processingEnv, viewMeta, (TypeElement) candidate, targetElement)) {
                            out.add((TypeElement) candidate);
                            break;
                        }
                    }
                }
            }
        }
        if (!out.isEmpty()) {
            out.sort(Comparator.comparing(e -> e.getQualifiedName().toString()));
            return out.get(0);
        } else {
            return null;
        }
    }
}
