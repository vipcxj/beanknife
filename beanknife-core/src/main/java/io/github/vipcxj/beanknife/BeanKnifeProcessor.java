package io.github.vipcxj.beanknife;

import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("io.github.vipcxj.beanknife.BeanKnife")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
@AutoService(Processor.class)
public class BeanKnifeProcessor extends AbstractProcessor {

    private final static String INDENT = "    ";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
            for (Element element : elements) {
                if (!element.getModifiers().contains(Modifier.ABSTRACT) && element.getKind() == ElementKind.CLASS) {
                    TypeElement typeElement = (TypeElement) element;
                    String qualifiedName = typeElement.getQualifiedName().toString();
                    List<Property> properties = collectProperties(typeElement);
                    try {
                        writeBuilderFile(qualifiedName, properties);
                    } catch (IOException e) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                    }
                }
            }
        }
        return true;
    }

    private boolean isObjectType(Element e) {
        return e.getKind() == ElementKind.CLASS && "java.lang.Object".equals(((TypeElement) e).getQualifiedName().toString());
    }

    private List<Property> collectProperties(TypeElement element) {
        List<Property> properties = new LinkedList<>();
        TypeMirror superclass = element.getSuperclass();
        if (superclass.getKind() != TypeKind.NONE) {
            Element parent = ((DeclaredType) superclass).asElement();
            if (!isObjectType(parent)) {
                List<Property> parentProperties = collectProperties((TypeElement) parent);
                properties.addAll(parentProperties);
            }
        }
        List<Property> scannedProperties = new PropertyElementVisitor<Void>().scan(element);
        for (Property scannedProperty : scannedProperties) {
            PropertyElementHelper.addProperty(properties, scannedProperty, false);
        }
        return properties;
    }

    private void writeBuilderFile(String className, List<Property> properties) throws IOException {
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        // String simpleClassName = className.substring(lastDot + 1);
        String metaClassName = className + "Meta";
        String metaSimpleClassName = metaClassName.substring(lastDot + 1);
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(metaClassName);
        try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
            if (packageName != null) {
                writer.print("package ");
                writer.print(packageName);
                writer.println(";");
                writer.println();
            }
            writer.print("public class ");
            writer.print(metaSimpleClassName);
            writer.println(" {");
            for (Property property : properties) {
                writer.print(INDENT);
                writer.print("public final String ");
                writer.print(property.getName());
                writer.print(" = \"");
                writer.print(property.getName());
                writer.print("\";");
                writer.println();
            }
            writer.println("}");
        }
    }
}
