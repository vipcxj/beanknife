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
@SupportedSourceVersion(SourceVersion.RELEASE_11)
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
                    Set<String> targetClassNames = new HashSet<>();
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        TypeElement typeElement = (TypeElement) element;
                        String qualifiedName = typeElement.getQualifiedName().toString();
                        ClassName targetClassName = extractClassName(annotationMirror, qualifiedName);
                        if (!targetClassNames.contains(targetClassName.getClassName())) {
                            targetClassNames.add(targetClassName.getClassName());
                            ClassName baseClassName = ClassName.parse(qualifiedName);
                            boolean samePackage = baseClassName.getPackageName().equals(targetClassName.getPackageName());
                            List<Property> properties = collectProperties(typeElement, samePackage);
                            try {
                                writeBuilderFile(targetClassName, properties);
                            } catch (IOException e) {
                                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                            }
                        } else {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING, "Repeated ViewMeta annotation with class name: " + targetClassName.getClassName() + ".");
                        }
                    }
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

    private ClassName extractClassName(AnnotationMirror annotation, String baseClassName) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues = processingEnv.getElementUtils().getElementValuesWithDefaults(annotation);
        String simpleClassName = Utils.getStringAnnotationValue(annotation, annotationValues, "value");
        String packageName = Utils.getStringAnnotationValue(annotation, annotationValues, "packageName");
        String className;
        if (simpleClassName.isEmpty()) {
            if (packageName.isEmpty()) {
                className = baseClassName + "Meta";
                return ClassName.parse(className);
            } else {
                ClassName csName = ClassName.parse(baseClassName);
                simpleClassName = csName.getSimpleClassName() + "Meta";
                return new ClassName(packageName, simpleClassName);
            }
        } else {
            ClassName csName = ClassName.parse(baseClassName);
            if (packageName.isEmpty()) {
                packageName = csName.getPackageName();
            }
            return new ClassName(packageName, simpleClassName);
        }
    }

    private void writeBuilderFile(ClassName csName, List<Property> properties) throws IOException {
        String metaClassName = csName.getClassName();
        String metaPackageName = csName.getPackageName();
        String metaSimpleClassName = csName.getSimpleClassName();
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
            writer.print(metaSimpleClassName);
            writer.println(" {");
            for (Property property : properties) {
                String variableName = Utils.createValidFieldName(property.getName(), names);
                names.add(variableName);
                writer.print(INDENT);
                writer.print("public final String ");
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
