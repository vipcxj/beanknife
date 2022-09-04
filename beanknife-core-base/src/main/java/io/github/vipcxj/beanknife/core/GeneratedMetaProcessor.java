package io.github.vipcxj.beanknife.core;

import com.sun.source.util.Trees;
import io.github.vipcxj.beanknife.core.models.ProcessorData;
import io.github.vipcxj.beanknife.core.models.ViewContext;
import io.github.vipcxj.beanknife.core.models.ViewOfData;
import io.github.vipcxj.beanknife.core.utils.AnnotationUtils;
import io.github.vipcxj.beanknife.core.utils.Constants;
import io.github.vipcxj.beanknife.core.utils.JetbrainUtils;
import io.github.vipcxj.beanknife.core.utils.Utils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes({"io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedMeta"})
public class GeneratedMetaProcessor extends AbstractProcessor {

    private ProcessorData processorData;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        System.out.println("GeneratedMetaProcessor Init");
        super.init(processingEnv);
        ProcessingEnvironment unwrappedProcessingEnv = JetbrainUtils.jbUnwrap(ProcessingEnvironment.class, this.processingEnv);
        Trees trees = Trees.instance(unwrappedProcessingEnv);
        this.processorData = new ProcessorData(trees, unwrappedProcessingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            this.processorData.clearViewContextMap();
            this.processorData.collect(roundEnv);
            for (TypeElement annotation : annotations) {
                // this.processorData.fix(processingEnv);
                Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
                for (Element element : elements) {
                    List<? extends AnnotationMirror> annotationMirrors = processingEnv.getElementUtils().getAllAnnotationMirrors(element);
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        if (Utils.isThisAnnotation(annotationMirror, Constants.GENERATED_META_TYPE_NAME)) {
                            Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues = processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);
                            TypeElement targetElement = (TypeElement) AnnotationUtils.<DeclaredType>getTypeAnnotationValue(annotationMirror, annotationValues, "targetClass").asElement();
                            List<DeclaredType> proxies = AnnotationUtils.getTypeListAnnotationValue(annotationMirror, annotationValues, "proxies");
                            for (DeclaredType proxy : proxies) {
                                TypeElement proxyElement = (TypeElement) proxy.asElement();
                                List<ViewOfData> viewOfs = this.processorData.getByConfigElement(proxyElement);
                                for (ViewOfData viewOf : viewOfs) {
                                    processViewOf(this.processorData, viewOf, targetElement);
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

    private void processViewOf(ProcessorData processorData, ViewOfData viewOfData, TypeElement targetElement) {
        if (viewOfData.getTargetElement().equals(targetElement)) {
            ViewContext context = processorData.getViewContext(viewOfData);
            try {
                Utils.writeViewFile(context);
            } catch (IOException e) {
                Utils.logError(processingEnv, e.getMessage());
            }
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
}
