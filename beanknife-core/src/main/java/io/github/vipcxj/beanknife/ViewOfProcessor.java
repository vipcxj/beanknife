package io.github.vipcxj.beanknife;

import com.google.auto.service.AutoService;
import io.github.vipcxj.beanknife.models.Context;
import io.github.vipcxj.beanknife.models.Property;
import io.github.vipcxj.beanknife.models.Type;
import io.github.vipcxj.beanknife.models.ViewOfData;
import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SupportedAnnotationTypes("io.github.vipcxj.beanknife.annotations.ViewOf")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class ViewOfProcessor extends AbstractProcessor {

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
                            "io.github.vipcxj.beanknife.annotations.ViewOf",
                            "io.github.vipcxj.beanknife.annotations.ViewOfs"
                    );
                    TypeElement typeElement = (TypeElement) element;
                    Set<String> targetClassNames = new HashSet<>();
                    for (AnnotationMirror annotationMirror : annotationMirrors) {
                        ViewOfData viewOf = ViewOfData.read(processingEnv, annotationMirror);
                        TypeElement targetElement = (TypeElement) viewOf.getValue().asElement();
                        if ("io.github.vipcxj.beanknife.utils.Self".equals(targetElement.getQualifiedName().toString())) {
                            targetElement = typeElement;
                        }
                        Type targetClassName = Type.extract(targetElement.asType());
                        Type genClassName = Utils.extractClassName(
                                targetClassName,
                                viewOf.getGenName(),
                                viewOf.getGenPackage(),
                                "View"
                        );
                        boolean samePackage = targetClassName.getPackageName().equals(genClassName.getPackageName());
                        String genQualifiedName = genClassName.getQualifiedName();
                        if (!targetClassNames.contains(genQualifiedName)) {
                            targetClassNames.add(genQualifiedName);
                            if (!Utils.canSeeFromOtherClass(typeElement, samePackage)) {
                                Utils.logError(
                                        processingEnv,
                                        "The target class "
                                                + targetClassName.getQualifiedName()
                                                + " can not be seen by the generated class "
                                                + genQualifiedName
                                                + "."
                                );
                                continue;
                            }

                            List<Property> properties = Utils.collectPropertiesFromBase(processingEnv, viewOf, targetElement, samePackage);
                            properties = filterProperties(viewOf, properties);
                            try {
                                writeBuilderFile(viewOf, targetClassName, genClassName, properties);
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

    private static List<Property> filterProperties(ViewOfData viewOf, List<Property> properties) {
        List<Property> filteredProperties = new ArrayList<>();
        List<Pattern> includePatterns = Arrays.stream(viewOf.getIncludePattern().split(",\\s")).map(Pattern::compile).collect(Collectors.toList());
        List<Pattern> excludePatterns = Arrays.stream(viewOf.getExcludePattern().split(",\\s")).map(Pattern::compile).collect(Collectors.toList());
        for (Property property : properties) {
            if (
                    (includePatterns.stream().anyMatch(pattern -> pattern.matcher(property.getName()).matches())
                            || Arrays.stream(viewOf.getIncludes()).anyMatch(include -> include.equals(property.getName())))
                    && excludePatterns.stream().noneMatch(pattern -> pattern.matcher(property.getName()).matches())
                    && Arrays.stream(viewOf.getExcludes()).noneMatch(include -> include.equals(property.getName()))
            ) {
                filteredProperties.add(property);
            }
        }
        return filteredProperties;
    }

    private void printReader(
            @Nonnull PrintWriter writer,
            Context context,
            ViewOfData viewOf,
            Type baseType,
            Type genType,
            List<Property> properties,
            boolean hasEmptyConstructor,
            boolean hasFieldsConstructor
    ) {
        Modifier modifier = viewOf.getReadMethod();
        if (modifier == null) {
            return;
        }
        writer.println();
        Utils.printIndent(writer, INDENT, 1);
        Utils.printModifier(writer, modifier);
        writer.print("static ");
        if (!genType.getParameters().isEmpty()) {
            genType.printGenericParameters(writer, context, true);
            writer.print(" ");
        }
        genType.printType(writer, context, true, false);
        writer.print(" read(");
        baseType.printType(writer, context, true, false);
        writer.println(" source) {");
        if (hasEmptyConstructor) {
            Utils.printIndent(writer, INDENT, 2);
            genType.printType(writer, context, true, false);
            writer.print(" out = new ");
            writer.print(genType.getSimpleName());
            if (!genType.getParameters().isEmpty()) {
                writer.print("<>");
            }
            writer.println("();");
            for (Property property : properties) {
                Utils.printIndent(writer, INDENT, 2);
                writer.print("out.");
                writer.print(context.getMappedFieldName(property));
                writer.print(" = ");
                if (property.isMethod()) {
                    writer.print("source.");
                    writer.print(property.getGetterName());
                    writer.println("();");
                } else {
                    writer.print("source.");
                    writer.print(property.getName());
                    writer.println(";");
                }
            }
            Utils.printIndent(writer, INDENT, 2);
            writer.println("return out;");
        } else if (hasFieldsConstructor) {
            Utils.printIndent(writer, INDENT, 2);
            writer.print("return new ");
            writer.print(genType.getSimpleName());
            if (!genType.getParameters().isEmpty()) {
                writer.print("<>");
            }
            writer.println("(");
            int i = 0;
            for (Property property : properties) {
                Utils.printIndent(writer, INDENT, 3);
                if (property.isMethod()) {
                    writer.print("source.");
                    writer.print(property.getGetterName());
                    writer.print("()");
                } else {
                    writer.print("source.");
                    writer.print(property.getName());
                    writer.print("");
                }
                if (i != properties.size() - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
                ++i;
            }
            Utils.printIndent(writer, INDENT, 2);
            writer.println(")");
        }
        Utils.printIndent(writer, INDENT, 1);
        writer.println("}");
    }

    private void writeBuilderFile(ViewOfData viewOf, Type baseType, Type genClassName, List<Property> properties) throws IOException {
        Modifier modifier = viewOf.getAccess();
        if (modifier == null) {
            return;
        }
        JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(genClassName.getFlatQualifiedName());
        try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
            Context context = new Context(genClassName.getPackageName());
            context.importVariable(baseType);
            for (Property property : properties) {
                context.importVariable(property.getType());
            }
            if (context.print(writer)) {
                writer.println();
            }
            context.enter(genClassName);
            genClassName.openClass(writer, modifier, context, INDENT, 0);
            if (!properties.isEmpty()) {
                writer.println();
                for (Property property : properties) {
                    property.printField(writer, context, INDENT, 1);
                }
            }
            boolean hasEmptyConstructor = false;
            boolean hasFieldsConstructor = false;
            modifier = viewOf.getEmptyConstructor();
            if (modifier != null) {
                hasEmptyConstructor = true;
                writer.println();
                genClassName.emptyConstructor(writer, modifier, INDENT, 1);
            }
            modifier = viewOf.getFieldsConstructor();
            if (modifier != null && !properties.isEmpty()) {
                hasFieldsConstructor = true;
                writer.println();
                genClassName.fieldsConstructor(writer, context, modifier, properties, INDENT, 1);
            }
            modifier = viewOf.getCopyConstructor();
            if (modifier != null) {
                writer.println();
                genClassName.copyConstructor(writer, context, modifier, properties, INDENT, 1);
            }
            printReader(writer, context, viewOf, baseType, genClassName, properties, hasEmptyConstructor, hasFieldsConstructor);
            for (Property property : properties) {
                writer.println();
                property.printGetter(writer, context, INDENT, 1);
            }
            context.exit();
            genClassName.closeClass(writer, INDENT, 0);
        }
    }
}
