package io.github.vipcxj.beanknife;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@SupportedAnnotationTypes({"io.github.vipcxj.beanknife.ViewMeta", "io.github.vipcxj.beanknife.ViewMetas"})
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
                            "io.github.vipcxj.beanknife.ViewMeta",
                            "io.github.vipcxj.beanknife.ViewMetas"
                    );
                    TypeElement typeElement = (TypeElement) element;
                    List<Property> samePackageProperties = null;
                    List<Property> diffPackageProperties = null;
                    Set<String> targetClassNames = new HashSet<>();
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        ClassName baseClassName = ClassName.extract(processingEnv, typeElement);
                        ClassName targetClassName = extractClassName(annotationMirror, baseClassName);
                        boolean samePackage = baseClassName.getPackageName().equals(targetClassName.getPackageName());
                        if (!targetClassNames.contains(targetClassName.getQualifiedClassName())) {
                            targetClassNames.add(targetClassName.getQualifiedClassName());
                            if (!Utils.canSeeFromOtherClass(typeElement, samePackage)) {
                                processingEnv.getMessager().printMessage(
                                        Diagnostic.Kind.ERROR,
                                        "The target class "
                                                + baseClassName.getQualifiedClassName()
                                                + " can not be seen by the generated class "
                                                + targetClassName.getQualifiedClassName()
                                                + "."
                                );
                                continue;
                            }

                            if (samePackage && samePackageProperties == null) {
                                samePackageProperties = collectProperties(typeElement, true);
                            } else if (!samePackage && diffPackageProperties == null) {
                                diffPackageProperties = collectProperties(typeElement, false);
                            }
                            List<Property> properties = samePackage ? samePackageProperties : diffPackageProperties;
                            try {
                                writeBuilderFile(targetClassName, properties);
                            } catch (IOException e) {
                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                            }
                        } else {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Repeated ViewMeta annotation with class name: " + targetClassName.getQualifiedClassName() + ".");
                        }
                    }
                } else {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "");
                }
            }
        }
        return true;
    }

    private List<Property> collectProperties( TypeElement element, boolean samePackage) {
        List<Property> properties = new LinkedList<>();
        Elements elementUtils = processingEnv.getElementUtils();
        List<? extends Element> members = elementUtils.getAllMembers(element);
        for (Element member : members) {
            Property property = null;
            if (member.getKind() == ElementKind.FIELD) {
                property = Utils.createProperty(processingEnv, (VariableElement) member);
            } else if (member.getKind() == ElementKind.METHOD) {
                property = Utils.createProperty(processingEnv, (ExecutableElement) member);
            }
            if (property != null) {
                Utils.addProperty(processingEnv, properties, property, samePackage, false);
            }
        }
        return properties;
    }

    private ClassName extractClassName(AnnotationMirror annotation, ClassName baseClassName) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues = processingEnv.getElementUtils().getElementValuesWithDefaults(annotation);
        String simpleClassName = Utils.getStringAnnotationValue(annotation, annotationValues, "value");
        String packageName = Utils.getStringAnnotationValue(annotation, annotationValues, "packageName");
        if (simpleClassName.isEmpty()) {
            if (packageName.isEmpty()) {
                return new ClassName(baseClassName.getPackageName(), baseClassName.getSimpleName() + "Meta");
            } else {
                simpleClassName = baseClassName.getSimpleName() + "Meta";
                return new ClassName(packageName, simpleClassName);
            }
        } else {
            if (packageName.isEmpty()) {
                packageName = baseClassName.getPackageName();
            }
            return new ClassName(packageName, simpleClassName);
        }
    }

    private void writeBuilderFile(ClassName csName, List<Property> properties) throws IOException {
        String metaClassName = csName.getFlatQualifiedClassName();
        String metaPackageName = csName.getPackageName();
        String metaDeclaredSimpleClassName = csName.getFlatSimpleName();
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
