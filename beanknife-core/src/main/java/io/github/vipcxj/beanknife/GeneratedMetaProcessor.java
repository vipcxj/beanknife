package io.github.vipcxj.beanknife;

import com.google.auto.service.AutoService;
import io.github.vipcxj.beanknife.annotations.internal.GeneratedMeta;
import io.github.vipcxj.beanknife.annotations.ViewOf;
import io.github.vipcxj.beanknife.annotations.ViewOfs;
import io.github.vipcxj.beanknife.models.ViewContext;
import io.github.vipcxj.beanknife.models.ViewOfData;
import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes({"io.github.vipcxj.beanknife.annotations.internal.GeneratedMeta"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class GeneratedMetaProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                List<? extends AnnotationMirror> annotationMirrors = processingEnv.getElementUtils().getAllAnnotationMirrors(element);
                for (AnnotationMirror annotationMirror : annotationMirrors) {
                    if (((TypeElement) annotationMirror.getAnnotationType().asElement()).getQualifiedName().toString().equals(GeneratedMeta.class.getCanonicalName())) {
                        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues = processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);
                        TypeElement targetElement = (TypeElement) Utils.getTypeAnnotationValue(annotationMirror, annotationValues, "targetClass").asElement();
                        DeclaredType[] proxies = Utils.getTypeArrayAnnotationValue(annotationMirror, annotationValues, "proxies");
                        for (DeclaredType proxy : proxies) {
                            TypeElement proxyElement = (TypeElement) proxy.asElement();
                            List<? extends AnnotationMirror> proxyAnnotations = processingEnv.getElementUtils().getAllAnnotationMirrors(proxyElement);
                            for (AnnotationMirror proxyAnnotation : proxyAnnotations) {
                                if (((TypeElement) proxyAnnotation.getAnnotationType().asElement()).getQualifiedName().toString().equals(ViewOf.class.getCanonicalName())) {
                                    processViewOf(proxyAnnotation, proxyElement, targetElement);
                                } else if (((TypeElement) proxyAnnotation.getAnnotationType().asElement()).getQualifiedName().toString().equals(ViewOfs.class.getCanonicalName())) {
                                    Map<? extends ExecutableElement, ? extends AnnotationValue> proxyAnnoValues = processingEnv.getElementUtils().getElementValuesWithDefaults(proxyAnnotation);
                                    List<AnnotationMirror> viewOfs = Utils.getAnnotationElement(proxyAnnotation, proxyAnnoValues);
                                    for (AnnotationMirror viewOf : viewOfs) {
                                        processViewOf(viewOf, proxyElement, targetElement);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private void processViewOf(AnnotationMirror viewOf, TypeElement sourceElement, TypeElement targetElement) {
        ViewOfData viewOfData = ViewOfData.read(processingEnv, viewOf, sourceElement);
        if (viewOfData.getTargetElement().equals(targetElement)) {
            ViewContext context = new ViewContext(processingEnv, viewOfData);
            try {
                Utils.writeViewFile(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
