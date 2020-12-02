package io.github.vipcxj.beanknife;

import com.google.auto.service.AutoService;
import io.github.vipcxj.beanknife.models.Property;
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

    private final static String INDENT = "    ";

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
                    TypeElement typeElement = (TypeElement) element;
                    List<Property> samePackageProperties = null;
                    List<Property> diffPackageProperties = null;
                    Set<String> targetClassNames = new HashSet<>();
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        ViewMetaData viewMeta = ViewMetaData.read(processingEnv, annotationMirror);
                        Type baseClassName = Type.extract(element.asType());
                        Type targetClassName = Utils.extractClassName(
                                baseClassName,
                                viewMeta.getValue(),
                                viewMeta.getPackageName(),
                                "Meta"
                        );
                        boolean samePackage = baseClassName.getPackageName().equals(targetClassName.getPackageName());
                        String targetQualifiedName = targetClassName.getQualifiedName();
                        if (!targetClassNames.contains(targetQualifiedName)) {
                            targetClassNames.add(targetQualifiedName);
                            if (!Utils.canSeeFromOtherClass(typeElement, samePackage)) {
                                Utils.logError(
                                        processingEnv,
                                        "The target class "
                                        + baseClassName.getQualifiedName()
                                        + " can not be seen by the generated class "
                                        + targetQualifiedName
                                        + "."
                                );
                                continue;
                            }

                            if (samePackage && samePackageProperties == null) {
                                samePackageProperties = Utils.collectProperties(processingEnv, typeElement, true);
                            } else if (!samePackage && diffPackageProperties == null) {
                                diffPackageProperties = Utils.collectProperties(processingEnv, typeElement, false);
                            }
                            List<Property> properties = samePackage ? samePackageProperties : diffPackageProperties;
                            try {
                                writeBuilderFile(targetClassName, properties);
                            } catch (IOException e) {
                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                            }
                        } else {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Repeated ViewMeta annotation with class name: " + targetQualifiedName + ".");
                        }
                    }
                } else {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "");
                }
            }
        }
        return true;
    }

    private void writeBuilderFile(Type csName, List<Property> properties) throws IOException {
        String metaClassName = csName.getFlatQualifiedName();
        String metaPackageName = csName.getPackageName();
        String metaDeclaredSimpleClassName = csName.getEnclosedFlatSimpleName();
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(metaClassName);
        Set<String> names = new HashSet<>();
        try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
            if (!metaPackageName.isEmpty()) {
                writer.print("package ");
                writer.print(metaPackageName);
                writer.println(";");
                writer.println();
            }
            writer.print("public class ");
            writer.print(metaDeclaredSimpleClassName);
            writer.println(" {");
            for (Property property : properties) {
                String variableName = Utils.createValidFieldName(property.getName(), names);
                names.add(variableName);
                writer.print(INDENT);
                writer.print("public static final String ");
                writer.print(variableName);
                writer.print(" = \"");
                writer.print(property.getName());
                writer.print("\";");
                writer.println();
            }
            writer.println("}");
        }
    }
}
