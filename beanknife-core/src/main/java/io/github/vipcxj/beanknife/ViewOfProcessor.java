package io.github.vipcxj.beanknife;

import com.google.auto.service.AutoService;
import io.github.vipcxj.beanknife.models.Property;
import io.github.vipcxj.beanknife.models.Type;
import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("io.github.vipcxj.beanknife.ViewOf")
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
                            "io.github.vipcxj.beanknife.ViewOf",
                            "io.github.vipcxj.beanknife.ViewOfs"
                    );
                    TypeElement typeElement = (TypeElement) element;
                    Set<String> targetClassNames = new HashSet<>();
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        TypeElement targetElement = getTargetElement(annotationMirror);
                        if ("io.github.vipcxj.beanknife.utils.Self".equals(targetElement.getQualifiedName().toString())) {
                            targetElement = typeElement;
                        }
                        Type targetClassName = Type.extract(targetElement.asType());
                        Type genClassName = Utils.extractClassName(
                                processingEnv,
                                annotationMirror,
                                targetClassName,
                                "genName",
                                "genPackage",
                                "View"
                        );
                        boolean samePackage = targetClassName.getPackageName().equals(genClassName.getPackageName());
                        String genQualifiedName = genClassName.getQualifiedName(false, false);
                        if (!targetClassNames.contains(genQualifiedName)) {
                            targetClassNames.add(genQualifiedName);
                            if (!Utils.canSeeFromOtherClass(typeElement, samePackage)) {
                                Utils.logError(
                                        processingEnv,
                                        "The target class "
                                                + targetClassName.getQualifiedName(false, false)
                                                + " can not be seen by the generated class "
                                                + genQualifiedName
                                                + "."
                                );
                                continue;
                            }

                            List<Property> properties = Utils.collectProperties(processingEnv, targetElement, samePackage);
                            try {
                                writeBuilderFile(genClassName, properties);
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

    private TypeElement getTargetElement(AnnotationMirror annotationMirror) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> annValues = processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);
        DeclaredType typeMirror = Utils.getTypeAnnotationValue(annotationMirror, annValues, "value");
        return (TypeElement) typeMirror.asElement();
    }

    private void writeBuilderFile(Type genClassName, List<Property> properties) throws IOException {

    }
}
