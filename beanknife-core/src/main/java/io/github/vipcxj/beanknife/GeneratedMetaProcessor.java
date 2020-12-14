package io.github.vipcxj.beanknife;

import com.google.auto.service.AutoService;
import com.sun.source.util.Trees;
import io.github.vipcxj.beanknife.annotations.internal.GeneratedMeta;
import io.github.vipcxj.beanknife.models.ViewContext;
import io.github.vipcxj.beanknife.models.ViewOfData;
import io.github.vipcxj.beanknife.models.ViewProcessorData;
import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes({"io.github.vipcxj.beanknife.annotations.internal.GeneratedMeta", "io.github.vipcxj.beanknife.annotations.ViewOf", "io.github.vipcxj.beanknife.annotations.ViewOfs"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class GeneratedMetaProcessor extends AbstractProcessor {

    private Trees trees;
    private ViewProcessorData viewProcessorData;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.trees = Trees.instance(processingEnv);
        this.viewProcessorData = new ViewProcessorData();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            this.viewProcessorData.collect(processingEnv, roundEnv);
            for (TypeElement annotation : annotations) {
                Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
                for (Element element : elements) {
                    List<? extends AnnotationMirror> annotationMirrors = processingEnv.getElementUtils().getAllAnnotationMirrors(element);
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        if (Utils.isThisAnnotation(annotationMirror, GeneratedMeta.class)) {
                            Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues = processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);
                            TypeElement targetElement = (TypeElement) Utils.getTypeAnnotationValue(annotationMirror, annotationValues, "targetClass").asElement();
                            DeclaredType[] proxies = Utils.getTypeArrayAnnotationValue(annotationMirror, annotationValues, "proxies");
                            for (DeclaredType proxy : proxies) {
                                TypeElement proxyElement = (TypeElement) proxy.asElement();
                                List<ViewOfData> viewOfs = this.viewProcessorData.getByConfigElement(proxyElement);
                                for (ViewOfData viewOf : viewOfs) {
                                    processViewOf(this.viewProcessorData, viewOf, targetElement);
                                }
                            }
                        }
                    }
                }
            }
            return true;
        } catch (Throwable t) {
            Utils.logError(processingEnv, t);
            return false;
        }
    }

    private void processViewOf(ViewProcessorData viewProcessorData, ViewOfData viewOfData, TypeElement targetElement) {
        if (viewOfData.getTargetElement().equals(targetElement)) {
            ViewContext context = new ViewContext(this.trees, processingEnv, viewOfData, viewProcessorData);
            try {
                Utils.writeViewFile(context);
            } catch (IOException e) {
                Utils.logError(processingEnv, e.getMessage());
            }
        }
    }

}
