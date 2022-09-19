package io.github.vipcxj.beanknife.core.utils;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.MetaCodeGenerators;
import io.github.vipcxj.beanknife.core.ViewCodeGenerators;
import io.github.vipcxj.beanknife.core.models.*;
import io.github.vipcxj.beanknife.core.spi.CodeGenerator;
import io.github.vipcxj.beanknife.core.spi.MetaCodeGenerator;
import io.github.vipcxj.beanknife.core.spi.ViewCodeGenerator;
import io.github.vipcxj.beanknife.runtime.annotations.Access;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOf;
import io.github.vipcxj.beanknife.runtime.annotations.ViewOfs;
import io.github.vipcxj.beanknife.runtime.annotations.ViewProperty;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedMeta;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedView;
import io.github.vipcxj.beanknife.runtime.utils.Self;

import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static io.github.vipcxj.beanknife.core.utils.AnnotationUtils.*;

public class Utils {

    public static String createGetterName(String fieldName, boolean isBoolean) {
        if (fieldName.isEmpty()) {
            throw new IllegalArgumentException("Empty field name is illegal.");
        }
        if (isBoolean && fieldName.length() >= 3 && fieldName.startsWith("is") && Character.isUpperCase(fieldName.charAt(2))) {
            return fieldName;
        }
        if (fieldName.length() == 1) {
            return isBoolean ? "is" + fieldName.toUpperCase() : "get" + fieldName.toUpperCase();
        } else if (Character.isUpperCase(fieldName.charAt(1))){
            return isBoolean ? "is" + fieldName : "get" + fieldName;
        } else {
            String part = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            return isBoolean ? "is" + part : "get" + part;
        }
    }

    public static String createSetterName(String fieldName, boolean isBoolean) {
        if (fieldName.isEmpty()) {
            throw new IllegalArgumentException("Empty field name is illegal.");
        }
        if (isBoolean && fieldName.length() >= 3 && fieldName.startsWith("is") && Character.isUpperCase(fieldName.charAt(2))) {
            return fieldName.replace("is", "set");
        }
        if (fieldName.length() == 1) {
            return "set" + fieldName.toUpperCase();
        } else if (Character.isUpperCase(fieldName.charAt(1))) {
            return "set" + fieldName;
        } else {
            String part = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            return "set" + part;
        }
    }

    public static String extractFieldName(String getterName) {
        boolean startsWithGet = getterName.startsWith("get");
        boolean startsWithIs = getterName.startsWith("is");
        if (startsWithGet || startsWithIs) {
            int preLen = startsWithGet ? 3 : 2;
            String part = getterName.substring(preLen);
            if (part.isEmpty()) {
                return null;
            }
            if (part.length() >= 2 && Character.isUpperCase(part.charAt(0)) && Character.isUpperCase(part.charAt(1))) {
                return part;
            }
            if (part.length() == 1) {
                return part.toLowerCase();
            } else {
                return part.substring(0, 1).toLowerCase() + part.substring(1);
            }
        }
        return null;
    }

    public static String createValidFieldName(String name, Set<String> names) {
        name = name.replaceAll("\\.", "_");
        if (SourceVersion.isKeyword(name) || names.contains(name)) {
            return createValidFieldName(name + "_", names);
        } else {
            return name;
        }
    }

    public static Modifier getPropertyModifier(Element e) {
        Set<Modifier> modifiers = e.getModifiers();
        Modifier modifier = Modifier.DEFAULT;
        if (modifiers.contains(Modifier.PUBLIC)) {
            modifier = Modifier.PUBLIC;
        } else if (modifiers.contains(Modifier.PROTECTED)){
            modifier = Modifier.PROTECTED;
        } else if (modifiers.contains(Modifier.PRIVATE)) {
            modifier = Modifier.PRIVATE;
        }
        return modifier;
    }

    public static ExecutableElement getSetterMethod(ProcessingEnvironment environment, String setterName, TypeMirror type, List<? extends Element> members) {
        Types types = environment.getTypeUtils();
        for (Element member : members) {
            Set<Modifier> modifiers = member.getModifiers();
            if (member.getKind() == ElementKind.METHOD
                    && !modifiers.contains(Modifier.STATIC)
                    && !modifiers.contains(Modifier.ABSTRACT)
                    && setterName.equals(member.getSimpleName().toString())
            ) {
                ExecutableElement setter = (ExecutableElement) member;
                List<? extends VariableElement> parameters = setter.getParameters();
                if (parameters.size() != 1) {
                    continue;
                }
                VariableElement variableElement = parameters.get(0);
                // todo: replace types
                if (!types.isSameType(variableElement.asType(), type)) {
                    continue;
                }
                if (setter.getReturnType().getKind() == TypeKind.VOID) {
                    return setter;
                }
            }
        }
        return null;
    }

    public static Access resolveGetterAccess(@CheckForNull ViewOfData viewOf, @NonNull Access access) {
        Access getter = viewOf != null ? viewOf.getGetters() : Access.UNKNOWN;
        if (access != Access.UNKNOWN) {
            getter = access;
        }
        return getter == Access.UNKNOWN ? Access.PUBLIC : getter;
    }

    public static Access resolveSetterAccess(@CheckForNull ViewOfData viewOf, @NonNull Access access) {
        Access setter = viewOf != null ? viewOf.getSetters() : Access.UNKNOWN;
        if (access != Access.UNKNOWN) {
            setter = access;
        }
        return setter == Access.UNKNOWN ? Access.NONE : setter;
    }

    public static Property createPropertyFromBase(
            @NonNull Context context,
            @CheckForNull ViewOfData viewOf,
            @NonNull VariableElement e,
            Access typeLombokGetter,
            Access typeLombokSetter
    ) {
        if (e.getKind() != ElementKind.FIELD) {
            return null;
        }
        Modifier modifier = getPropertyModifier(e);
        String name = e.getSimpleName().toString();
        ViewProperty viewProperty = e.getAnnotation(ViewProperty.class);
        Access getter = viewProperty != null ? viewProperty.getter() : Access.UNKNOWN;
        Access setter = viewProperty != null ? viewProperty.setter() : Access.UNKNOWN;
        TypeMirror type = e.asType();
        String setterName = createSetterName(name, type.getKind() == TypeKind.BOOLEAN);
        LombokInfo lombokInfo = new LombokInfo(e, typeLombokGetter, typeLombokSetter);
        return new Property(
                name,
                true,
                modifier,
                resolveGetterAccess(viewOf, getter),
                resolveSetterAccess(viewOf, setter),
                Type.extract(context, e, null),
                false,
                createGetterName(name, type.getKind() == TypeKind.BOOLEAN),
                setterName,
                e,
                context.getProcessingEnv().getElementUtils().getDocComment(e),
                lombokInfo
        );
    }

    public static Property createPropertyFromBase(
            @NonNull Context context,
            @CheckForNull ViewOfData viewOf,
            @NonNull ExecutableElement e
    ) {
        if (e.getKind() != ElementKind.METHOD) {
            return null;
        }
        if (!e.getParameters().isEmpty()) {
            return null;
        }
        TypeMirror type = e.getReturnType();
        if (type.getKind() == TypeKind.VOID) {
            return null;
        }
        String methodName = e.getSimpleName().toString();
        String name = extractFieldName(methodName);
        if (name == null) {
            return null;
        }
        ViewProperty viewProperty = e.getAnnotation(ViewProperty.class);
        String setterName = createSetterName(name, type.getKind() == TypeKind.BOOLEAN);
        return new Property(
                name,
                true,
                getPropertyModifier(e),
                resolveGetterAccess(viewOf, viewProperty != null ? viewProperty.getter() : Access.UNKNOWN),
                resolveSetterAccess(viewOf, viewProperty != null ? viewProperty.setter() : Access.UNKNOWN),
                Type.extract(context, e, null),
                true,
                methodName,
                setterName,
                e,
                context.getProcessingEnv().getElementUtils().getDocComment(e),
                null
        );
    }

    public static boolean canSeeFromOtherClass(Modifier modifier, boolean samePackage) {
        if (samePackage) {
            return modifier != Modifier.PRIVATE;
        } else {
            return modifier == Modifier.PUBLIC;
        }
    }

    public static boolean canNotSeeFromOtherClass(Property property, boolean samePackage) {
        if (property.hasLombokGetter()) {
            return !property.isLombokReadable(samePackage);
        } else {
            return !canSeeFromOtherClass(property.getModifier(), samePackage);
        }
    }

    public static boolean canSeeFromOtherClass(Element element, boolean samePackage) {
        if (samePackage) {
            return !element.getModifiers().contains(Modifier.PRIVATE);
        } else {
            return element.getModifiers().contains(Modifier.PUBLIC);
        }
    }

    public static boolean isObjectType(Element element) {
        return element.getKind() == ElementKind.CLASS && ((TypeElement) element).getQualifiedName().contentEquals(Constants.TYPE_OBJECT);
    }

    public static boolean isNotObjectProperty(Property property) {
        Element parent = property.getElement().getEnclosingElement();
        return !isObjectType(parent);
    }

    private static String getFlatQualifiedName(TypeElement element) {
        if (element.getNestingKind() == NestingKind.TOP_LEVEL) {
            return element.getQualifiedName().toString();
        } else {
            Element parent = element.getEnclosingElement();
            return getFlatQualifiedName((TypeElement) parent) + "$" + element.getSimpleName();
        }
    }

    public static String extractGenTypeName(TypeElement baseTypeElement, String genName, String genPackage, String postfix) {
        if (genName.isEmpty()) {
            if (genPackage.isEmpty()) {
                return getFlatQualifiedName(baseTypeElement) + postfix;
            } else {
                String baseName = getFlatQualifiedName(baseTypeElement);
                int index = baseName.lastIndexOf('.');
                if (index != -1) {
                    return genPackage+ '.' + baseName.substring(index + 1) + postfix;
                } else {
                    return genPackage + '.'  + baseName + postfix;
                }
            }
        } else {
            if (genPackage.isEmpty()) {
                String baseName = getFlatQualifiedName(baseTypeElement);
                int index = baseName.lastIndexOf('.');
                return baseName.substring(0, index) + '.' + genName;
            } else {
                return genPackage + '.' + genName;
            }
        }
    }

    public static Type extractGenType(@NonNull Type baseClassName, @NonNull String genName, @NonNull String genPackage, @NonNull String postfix) {
        if (genName.isEmpty()) {
            if (genPackage.isEmpty()) {
                return baseClassName.appendName(postfix).flatten();
            } else {
                return baseClassName.changePackage(genPackage).appendName(postfix).flatten();
            }
        } else {
            if (genPackage.isEmpty()) {
                return baseClassName.changeSimpleName(genName, true);
            } else {
                return baseClassName.changePackage(genPackage).changeSimpleName(genName, true);
            }
        }
    }

    public static void logWarn(ProcessingEnvironment env, String message) {
        env.getMessager().printMessage(Diagnostic.Kind.WARNING, message);
    }

    public static void logError(ProcessingEnvironment env, String message) {
        env.getMessager().printMessage(Diagnostic.Kind.ERROR, message != null ? message : "");
        System.err.println(message);
    }

    public static void logError(ProcessingEnvironment env, Throwable t) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        t.printStackTrace(writer);
        env.getMessager().printMessage(Diagnostic.Kind.ERROR, stringWriter.toString());
        System.err.println(stringWriter);
    }

    public static void printIndent(@NonNull PrintWriter writer, String indent, int num) {
        for (int i = 0; i < num; ++i) {
            writer.print(indent);
        }
    }

    public static void printModifier(@NonNull PrintWriter writer, @NonNull Modifier modifier) {
        if (modifier != Modifier.DEFAULT) {
            writer.print(modifier);
            writer.print(" ");
        }
    }

    public static void printAccess(@NonNull PrintWriter writer, @NonNull Access access) {
        if (access == Access.UNKNOWN) {
            throw new IllegalArgumentException("Unknown access is not supported.");
        }
        if (access != Access.NONE && access != Access.DEFAULT) {
            writer.print(access);
            writer.print(" ");
        }
    }

    public static boolean shouldIgnoredElement(Element element) {
        GeneratedMeta generatedMeta = element.getAnnotation(GeneratedMeta.class);
        if (generatedMeta != null) {
            return true;
        }
        GeneratedView generatedView = element.getAnnotation(GeneratedView.class);
        if (generatedView != null) {
            return true;
        }
        return element.getKind() != ElementKind.CLASS;
    }

    public static boolean isViewMetaTargetTo(ViewMetaData viewMeta, TypeElement targetElement, String packageName) {
        return viewMeta.getOf().equals(targetElement) && viewMeta.getPackageName().equals(packageName);
    }

    public static List<TypeElement> calcDependencies(TypeElement element) {
        List<TypeElement> dependencies = new ArrayList<>();
        dependencies.add(element);
        TypeMirror superclass = element.getSuperclass();
        if (superclass.getKind() != TypeKind.NONE) {
           DeclaredType declaredType = (DeclaredType) superclass;
           dependencies.addAll(calcDependencies(Utils.toElement(declaredType)));
        }
        return dependencies;
    }

    private static Element[] calcDependencies(ViewContext context) {
        ViewOfData viewOf = context.getViewOf();
        List<TypeElement> targetDependencies = calcDependencies(viewOf.getTargetElement());
        List<TypeElement> configDependencies;
        if (!viewOf.getConfigElement().equals(viewOf.getTargetElement())) {
            configDependencies = calcDependencies(viewOf.getConfigElement());
        } else {
            configDependencies = Collections.emptyList();
        }
        Element[] dependencies = new Element[targetDependencies.size() + configDependencies.size()];
        int i = 0;
        for (TypeElement targetDependency : targetDependencies) {
            dependencies[i++] = targetDependency;
        }
        for (TypeElement configDependency : configDependencies) {
            dependencies[i++] = configDependency;
        }
        return dependencies;
    }

    public static void writeViewFile(ViewContext context) throws IOException {
        Modifier modifier = context.getViewOf().getAccess();
        if (modifier == null) {
            return;
        }
        Element[] dependencies = calcDependencies(context);
        Filer filer = context.getProcessingEnv().getFiler();
        FileObject fileObject = filer.createSourceFile(context.getGenType().getQualifiedName(), dependencies);
        try (PrintWriter writer = new PrintWriter(fileObject.openWriter())) {
            context.print(writer);
        }
        for (ViewCodeGenerator generator : ViewCodeGenerators.INSTANCE.getGenerators()) {
            CodeGenerator.printOutside(generator, context, filer, dependencies);
        }
    }

    private static Element[] calcDependencies(MetaContext context) {
        ViewMetaData viewMetaData = context.getViewMeta();
        Set<TypeElement> dependencies = new HashSet<>();
        dependencies.addAll(calcDependencies(viewMetaData.getOf()));
        dependencies.addAll(calcDependencies(viewMetaData.getConfig()));
        return dependencies.toArray(new Element[0]);
    }

    public static void writeMetaFile(MetaContext context) throws IOException {
        Element[] dependencies = calcDependencies(context);
        JavaFileObject sourceFile = context.getProcessingEnv().getFiler().createSourceFile(context.getGenType().getQualifiedName(), dependencies);
        try (PrintWriter writer = new PrintWriter(sourceFile.openWriter())) {
            context.collectData();
            context.print(writer);
        }
        for (MetaCodeGenerator generator : MetaCodeGenerators.INSTANCE.getGenerators()) {
            CodeGenerator.printOutside(generator, context, context.getProcessingEnv().getFiler(), dependencies);
        }
    }

    public static void printComment(@NonNull PrintWriter writer, String comment, boolean getter, String indent, int indentNum) {
        if (comment == null || comment.isEmpty()) {
            return;
        }
        String[] lines = comment.split("[\\r\\n]+");
        if (lines.length == 0) {
            return;
        }
        Utils.printIndent(writer, indent, indentNum);
        writer.println("/**");
        for (String line : lines) {
            if (getter && line.trim().startsWith("@param ")) {
                continue;
            }
            Utils.printIndent(writer, indent, indentNum);
            writer.print(" * ");
            writer.println(line);
        }
        Utils.printIndent(writer, indent, indentNum);
        writer.println(" */");
    }

    public static boolean isThisType(@NonNull TypeMirror typeMirror, @NonNull Class<?> type) {
        if (typeMirror.getKind().isPrimitive()) {
            return type.isPrimitive()
                    &&((typeMirror.getKind() == TypeKind.BOOLEAN && type == boolean.class)
                    || (typeMirror.getKind() == TypeKind.BYTE && type == byte.class)
                    || (typeMirror.getKind() == TypeKind.CHAR && type == char.class)
                    || (typeMirror.getKind() == TypeKind.SHORT && type == short.class)
                    || (typeMirror.getKind() == TypeKind.INT && type == int.class)
                    || (typeMirror.getKind() == TypeKind.LONG && type == long.class)
                    || (typeMirror.getKind() == TypeKind.FLOAT && type == float.class)
                    || (typeMirror.getKind() == TypeKind.DOUBLE && type == double.class));
        } else if (typeMirror.getKind() == TypeKind.VOID) {
            return type == void.class;
        } else if (typeMirror.getKind() == TypeKind.ARRAY) {
            return type.isArray() && isThisType(((ArrayType) typeMirror).getComponentType(), type.getComponentType());
        } else if (typeMirror.getKind() == TypeKind.DECLARED) {
            String canonicalName = type.getCanonicalName();
            if (canonicalName == null) {
                throw new UnsupportedOperationException();
            }
            DeclaredType declaredType = (DeclaredType) typeMirror;
            return toElement(declaredType).getQualifiedName().toString().equals(canonicalName);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static boolean isThisTypeElement(@NonNull TypeElement typeElement, @NonNull Class<?> type) {
        return typeElement.getQualifiedName().toString().equals(type.getCanonicalName());
    }

    public static boolean isThisAnnotation(@NonNull AnnotationMirror annotation, @NonNull Class<?> type) {
        TypeElement element = (TypeElement) annotation.getAnnotationType().asElement();
        return element.getQualifiedName().toString().equals(type.getCanonicalName());
    }

    public static boolean isThisAnnotation(@NonNull AnnotationMirror annotation, @NonNull String typeName) {
        TypeElement element = (TypeElement) annotation.getAnnotationType().asElement();
        return element.getQualifiedName().toString().equals(typeName);
    }

    public static List<AnnotationMirror> getAnnotationsOn(@NonNull Elements elements, @NonNull Element element, @NonNull Class<?> type, @CheckForNull Class<?> repeatContainerType) {
        return getAnnotationsOn(elements, element, type, repeatContainerType, new HashSet<>(), true, true);
    }

    public static List<AnnotationMirror> getAnnotationsOn(@NonNull Elements elements, @NonNull Element element, @NonNull Class<?> type, @CheckForNull Class<?> repeatContainerType, boolean indirect) {
        return getAnnotationsOn(elements, element, type, repeatContainerType, new HashSet<>(), indirect, true);
    }

    public static List<AnnotationMirror> getAnnotationsOn(@NonNull Elements elements, @NonNull Element element, @NonNull Class<?> type, @CheckForNull Class<?> repeatContainerType, boolean indirect, boolean metaExtends) {
        return getAnnotationsOn(elements, element, type, repeatContainerType, new HashSet<>(), indirect, metaExtends);
    }

    public static List<AnnotationMirror> getAnnotationsOn(@NonNull Elements elements, @NonNull Element element, @NonNull String typeName, @CheckForNull String repeatContainerTypeName, boolean indirect, boolean metaExtends) {
        return getAnnotationsOn(elements, element, typeName, repeatContainerTypeName, new HashSet<>(), indirect, metaExtends);
    }

    private static List<AnnotationMirror> getAnnotationsOn(@NonNull Elements elements,  @NonNull Element element, @NonNull Class<?> type, @CheckForNull Class<?> repeatContainerType, @NonNull Set<Element> visited, boolean indirect, boolean metaExtends) {
        return getAnnotationsOn(elements, element, type.getName(), repeatContainerType != null ? repeatContainerType.getName() : null, visited, indirect, metaExtends);
    }

    private static List<AnnotationMirror> getAnnotationsOn(@NonNull Elements elements,  @NonNull Element element, @NonNull String typeName, @CheckForNull String repeatContainerTypeName, @NonNull Set<Element> visited, boolean indirect, boolean metaExtends) {
        if (visited.contains(element)) {
            return Collections.emptyList();
        }
        visited.add(element);
        List<AnnotationMirror> result = new ArrayList<>();
        List<? extends AnnotationMirror> allAnnotations = indirect ? getAllAnnotationMirrors(elements, element) : element.getAnnotationMirrors();
        for (AnnotationMirror annotation : allAnnotations) {
            if (isThisAnnotation(annotation, typeName)) {
                result.add(annotation);
            } else if (repeatContainerTypeName != null && isThisAnnotation(annotation, repeatContainerTypeName)){
                Map<? extends ExecutableElement, ? extends AnnotationValue> attributes = elements.getElementValuesWithDefaults(annotation);
                List<AnnotationMirror> annotations = AnnotationUtils.getAnnotationElement(annotation, attributes);
                result.addAll(annotations);
            } else if (metaExtends) {
                result.addAll(getAnnotationsOn(elements, annotation.getAnnotationType().asElement(), typeName, repeatContainerTypeName, visited, indirect, true));
            }
        }
        return result;
    }

    public static DeclaredType toType(TypeElement element) {
        return (DeclaredType) element.asType();
    }

    public static TypeElement toElement(DeclaredType type) {
        return (TypeElement) type.asElement();
    }

    public static DeclaredType findSuperType(DeclaredType theType, Class<?> type) {
        TypeElement element = toElement(theType);
        if (type.isInterface()) {
            for (TypeMirror anInterface : element.getInterfaces()) {
                if (isThisType(anInterface, type)) {
                    return (DeclaredType) anInterface;
                }
            }
        }
        TypeMirror superType = element.getSuperclass();
        if (superType.getKind() == TypeKind.NONE) {
            return null;
        }
        if (isThisType(superType, type)) {
            return (DeclaredType) superType;
        }
        return findSuperType((DeclaredType) superType, type);
    }

    public static boolean hasEmptyConstructor(ProcessingEnvironment env, TypeElement element) {
        ExecutableElement emptyConstructor = findMethod(env, element, "<init>", true);
        return emptyConstructor != null || findMethod(env, element, "<init>", false) == null;
    }

    public static ExecutableElement findMethod(ProcessingEnvironment env, TypeElement element, String name, boolean matchParameters, TypeMirror... argTypes) {
        Types typeUtils = env.getTypeUtils();
        for (Element member : env.getElementUtils().getAllMembers(element)) {
            if (member.getKind() == ElementKind.METHOD && member.getSimpleName().toString().equals(name)) {
                ExecutableElement executable = (ExecutableElement) member;
                List<? extends VariableElement> parameters = executable.getParameters();
                if (matchParameters) {
                    if (parameters.size() != argTypes.length) {
                        continue;
                    }
                    boolean matched = true;
                    for (int i = 0; i < parameters.size(); ++i) {
                        VariableElement variable = parameters.get(i);
                        TypeMirror argType = argTypes[i];
                        if (!typeUtils.isAssignable(argType, variable.asType())) {
                            matched = false;
                            break;
                        }
                    }
                    if (!matched) {
                        continue;
                    }
                }
                return executable;
            }
        }
        return null;
    }

    private static void collectViewOfs(List<ViewOfData> results, ProcessingEnvironment processingEnv, TypeElement candidate, TypeElement targetElement, String packageName, AnnotationMirror viewOf) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValuesWithDefaults = processingEnv.getElementUtils().getElementValuesWithDefaults(viewOf);
        DeclaredType targetType = getTypeAnnotationValue(viewOf, elementValuesWithDefaults, "value");
        TypeElement targetTypeElement = Utils.toElement(targetType);
        if (Utils.isThisTypeElement(targetTypeElement, Self.class)) {
            targetTypeElement = candidate;
        }
        if (!targetTypeElement.equals(targetElement)) {
            return;
        }
        ViewOfData viewOfData = ViewOfData.read(processingEnv, viewOf, candidate);
        if (!viewOfData.getMetaPackage().equals(packageName)) {
            return;
        }
        results.add(viewOfData);
    }

    private static void collectViewOfs(List<ViewOfData> results, ProcessingEnvironment processingEnv, TypeElement candidate, AnnotationMirror viewOf) {
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValuesWithDefaults = processingEnv.getElementUtils().getElementValuesWithDefaults(viewOf);
        TypeElement targetElement = toElement(AnnotationUtils.getTypeAnnotationValue(viewOf, elementValuesWithDefaults, "value"));
        if (isThisTypeElement(targetElement, Self.class)) {
            targetElement = candidate;
        }
        TypeElement configElement = toElement(AnnotationUtils.getTypeAnnotationValue(viewOf, elementValuesWithDefaults, "config"));
        if (isThisTypeElement(configElement, Self.class)) {
            configElement = candidate;
        }
        ViewOfData viewOfData = ViewOfData.read(processingEnv, viewOf, candidate);
        viewOfData.setConfigElement(configElement);
        viewOfData.setTargetElement(targetElement);
        results.add(viewOfData);
    }

    public static List<ViewOfData> collectViewOfs(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv) {
        Set<? extends Element> proxySources = roundEnv.getElementsAnnotatedWith(GeneratedMeta.class);
        Map<String, TypeElement> configClasses = new HashMap<>();
        for (Element proxySource : proxySources) {
            List<? extends AnnotationMirror> annotationMirrors = proxySource.getAnnotationMirrors();
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (Utils.isThisAnnotation(annotationMirror, GeneratedMeta.class)) {
                    Map<? extends ExecutableElement, ? extends AnnotationValue> elementValuesWithDefaults = processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);
                    List<DeclaredType> proxies = AnnotationUtils.getTypeListAnnotationValue(annotationMirror, elementValuesWithDefaults, "proxies");
                    for (DeclaredType proxy : proxies) {
                        TypeElement proxyElement = toElement(proxy);
                        configClasses.put(proxyElement.getQualifiedName().toString(), proxyElement);
                    }
                }
            }
        }
        List<ViewOfData> out = new ArrayList<>();
        for (TypeElement configElement : configClasses.values()) {
            List<? extends AnnotationMirror> annotationMirrors = configElement.getAnnotationMirrors();
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (Utils.isThisAnnotation(annotationMirror, ViewOf.class)) {
                    collectViewOfs(out, processingEnv, configElement, annotationMirror);
                }
            }
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (Utils.isThisAnnotation(annotationMirror, ViewOfs.class)) {
                    Map<? extends ExecutableElement, ? extends AnnotationValue> elementValuesWithDefaults = processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);
                    List<AnnotationMirror> viewOfs = AnnotationUtils.getAnnotationElement(annotationMirror, elementValuesWithDefaults);
                    for (AnnotationMirror viewOf : viewOfs) {
                        collectViewOfs(out, processingEnv, configElement, viewOf);
                    }
                }
            }
        }
        return out;
    }

    public static List<ViewOfData> collectViewOfs(ProcessingEnvironment processingEnv, RoundEnvironment roundEnv, TypeElement targetElement, String packageName) {
        Set<? extends Element> candidates = roundEnv.getElementsAnnotatedWith(ViewOf.class);
        List<ViewOfData> out = new ArrayList<>();
        for (Element candidate : candidates) {
            if (Utils.shouldIgnoredElement(candidate)) {
                continue;
            }
            List<? extends AnnotationMirror> annotationMirrors = candidate.getAnnotationMirrors();
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (Utils.isThisAnnotation(annotationMirror, ViewOf.class)) {
                    collectViewOfs(out, processingEnv, (TypeElement) candidate, targetElement, packageName, annotationMirror);
                }
            }
        }
        candidates = roundEnv.getElementsAnnotatedWith(ViewOfs.class);
        for (Element candidate : candidates) {
            if (Utils.shouldIgnoredElement(candidate)) {
                continue;
            }
            List<? extends AnnotationMirror> annotationMirrors = candidate.getAnnotationMirrors();
            for (AnnotationMirror annotationMirror : annotationMirrors) {
                if (Utils.isThisAnnotation(annotationMirror, ViewOfs.class)) {
                    Map<? extends ExecutableElement, ? extends AnnotationValue> elementValuesWithDefaults = processingEnv.getElementUtils().getElementValuesWithDefaults(annotationMirror);
                    List<AnnotationMirror> viewOfs = AnnotationUtils.getAnnotationElement(annotationMirror, elementValuesWithDefaults);
                    for (AnnotationMirror viewOf : viewOfs) {
                        collectViewOfs(out, processingEnv, (TypeElement) candidate, targetElement, packageName, viewOf);
                    }
                }
            }
        }
        return out;
    }

    public static Modifier accessToModifier(Access access) {
        switch (access) {
            case PUBLIC:
                return Modifier.PUBLIC;
            case DEFAULT:
                return Modifier.DEFAULT;
            case PROTECTED:
                return Modifier.PROTECTED;
            case PRIVATE:
            case UNKNOWN:
            case NONE:
                return Modifier.PRIVATE;
            default:
                throw new IllegalStateException("This is impossible!");
        }
    }

    public static Access accessFromModifier(Modifier modifier) {
        if (modifier == null) {
            return Access.NONE;
        }
        switch (modifier) {
            case PUBLIC:
                return Access.PUBLIC;
            case DEFAULT:
                return Access.DEFAULT;
            case PROTECTED:
                return Access.PROTECTED;
            case PRIVATE:
                return Access.PRIVATE;
            default:
                return Access.UNKNOWN;
        }
    }

    public static List<AnnotationMirror> getRepeatableAnnotationComponents(@NonNull Elements elements, @NonNull AnnotationMirror annotation) {
        TypeElement componentElement = getRepeatableAnnotationComponentElement(elements, annotation);
        if (componentElement != null) {
            Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues = elements.getElementValuesWithDefaults(annotation);
            return AnnotationUtils.getAnnotationListAnnotationValue(annotation, annotationValues, "value");
        }
        return null;
    }

    private static TypeElement getRepeatableAnnotationContainerElement(@NonNull Elements elements, @NonNull TypeElement element) {
        if (element.getKind() == ElementKind.ANNOTATION_TYPE) {
            List<AnnotationMirror> repeatableList = getAnnotationsOn(elements, element, Repeatable.class, null, false, false);
            if (!repeatableList.isEmpty()) {
                AnnotationMirror repeatable = repeatableList.get(0);
                Map<? extends ExecutableElement, ? extends AnnotationValue> annValues = repeatable.getElementValues();
                AnnotationValue annValue = annValues.values().iterator().next();
                DeclaredType repeatableAnnotationType = (DeclaredType) annValue.getValue();
                return toElement(repeatableAnnotationType);
            }
        }
        return null;
    }

    public static TypeElement getRepeatableAnnotationComponentElement(@NonNull Elements elements, AnnotationMirror annotation) {
        TypeElement typeElement = Utils.toElement(annotation.getAnnotationType());
        Map<? extends ExecutableElement, ? extends AnnotationValue> elementValues = elements.getElementValuesWithDefaults(annotation);
        if (elementValues.size() == 1) {
            ExecutableElement key = elementValues.keySet().iterator().next();
            if (key.getSimpleName().toString().equals("value")) {
                TypeMirror returnType = key.getReturnType();
                if (returnType.getKind() == TypeKind.ARRAY) {
                    ArrayType arrayType = (ArrayType) returnType;
                    TypeMirror componentType = arrayType.getComponentType();
                    if (componentType.getKind() == TypeKind.DECLARED) {
                        TypeElement componentElement = toElement((DeclaredType) componentType);
                        TypeElement containerElement = getRepeatableAnnotationContainerElement(elements, componentElement);
                        if (containerElement != null && containerElement.getQualifiedName().toString().equals(typeElement.getQualifiedName().toString())) {
                            return componentElement;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static boolean isAnnotationRepeatable(@NonNull Elements elements, AnnotationMirror annotation) {
        return !getAnnotationsOn(elements, annotation.getAnnotationType().asElement(), Repeatable.class, null, false, false).isEmpty();
    }

    public static boolean isAnnotationInheritable(@NonNull Elements elements, AnnotationMirror annotation) {
        List<AnnotationMirror> inheritedList = getAnnotationsOn(elements, annotation.getAnnotationType().asElement(), Inherited.class, null, false, false);
        if (!inheritedList.isEmpty()) {
            return true;
        }
        TypeElement componentElement = getRepeatableAnnotationComponentElement(elements, annotation);
        if (componentElement != null) {
            return !getAnnotationsOn(elements, componentElement, Inherited.class, null, false, false).isEmpty();
        } else {
            return false;
        }
    }

    @CheckForNull
    public static List<ElementType> getAnnotationTarget(@NonNull Elements elements, @NonNull AnnotationMirror annotation) {
        List<AnnotationMirror> targets = getAnnotationsOn(elements, annotation.getAnnotationType().asElement(), Target.class, null, false, false);
        if (targets.isEmpty()) {
            return null;
        }
        AnnotationMirror target = targets.get(0);
        Map<? extends ExecutableElement, ? extends AnnotationValue> values = elements.getElementValuesWithDefaults(target);
        return AnnotationUtils.getEnumListAnnotationValue(target, values, "value", ElementType.class);
    }

    public static boolean annotationCanPutOn(@NonNull Elements elements, @NonNull AnnotationMirror annotation, @NonNull ElementType elementType) {
        List<ElementType> types = getAnnotationTarget(elements, annotation);
        if (types == null) {
            return elementType != ElementType.TYPE_PARAMETER;
        }
        return types.contains(elementType);
    }

    private static void importTypeMirror(@NonNull Context context, @NonNull TypeMirror typeMirror) {
        if (typeMirror.getKind() == TypeKind.ARRAY) {
            ArrayType arrayType = (ArrayType) typeMirror;
            importTypeMirror(context, arrayType.getComponentType());
        } else if (typeMirror.getKind() == TypeKind.DECLARED) {
            DeclaredType declaredType = (DeclaredType) typeMirror;
            TypeElement typeElement = toElement(declaredType);
            Type type = Type.extract(context, typeElement);
            if (type != null) {
                context.importVariable(type);
            }
        }
    }

    private static void importAnnotationValue(@NonNull Context context, @NonNull AnnotationValue annValue) {
        AnnotationValueKind type = getAnnotationValueType(annValue);
        if (type == AnnotationValueKind.TYPE) {
            TypeMirror typeMirror = (TypeMirror) annValue.getValue();
            importTypeMirror(context, typeMirror);
        } else if (type == AnnotationValueKind.ANNOTATION) {
            AnnotationMirror annotationMirror = (AnnotationMirror) annValue.getValue();
            importAnnotation(context, annotationMirror);
        } else if (type == AnnotationValueKind.ENUM) {
            VariableElement enumValue = (VariableElement) annValue.getValue();
            TypeElement enumElement = (TypeElement) enumValue.getEnclosingElement();
            Type enumType = Type.extract(context, enumElement);
            if (enumType != null) {
                context.importVariable(enumType);
            }
        } else if (type == AnnotationValueKind.ARRAY) {
            //noinspection unchecked
            List<? extends AnnotationValue> arrayValues = (List<? extends AnnotationValue>) annValue.getValue();
            for (AnnotationValue arrayValue : arrayValues) {
                importAnnotationValue(context, arrayValue);
            }
        }
    }

    public static void importAnnotation(@NonNull Context context, @NonNull AnnotationMirror annotation) {
        Element element = annotation.getAnnotationType().asElement();
        Type type = Type.extract(context, element);
        if (type == null) {
            context.error("Unable to resolve the type " + element + ".");
            return;
        }
        context.importVariable(type);
        Map<? extends ExecutableElement, ? extends AnnotationValue> annValues = annotation.getElementValues();
        for (AnnotationValue annValue : annValues.values()) {
            importAnnotationValue(context, annValue);
        }
    }

    public static List<AnnotationMirror> getAllAnnotationMirrors(@NonNull Elements elements, @NonNull Element element) {
        return getAllAnnotationMirrors(elements, element, true);
    }

    private static List<AnnotationMirror> getAllAnnotationMirrors(@NonNull Elements elements, @NonNull Element element, boolean direct) {
        List<AnnotationMirror> results = new LinkedList<>();
        if (direct) {
            results.addAll(element.getAnnotationMirrors());
        } else {
            for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
             if (isAnnotationInheritable(elements, annotationMirror)) {
                 results.add(annotationMirror);
             }
            }
        }
        if (element.getKind() == ElementKind.CLASS) {
            TypeElement typeElement = (TypeElement) element;
            TypeMirror superclass = typeElement.getSuperclass();
            if (superclass.getKind() != TypeKind.NONE) {
                DeclaredType declaredType = (DeclaredType) superclass;
                results.addAll(0, getAllAnnotationMirrors(elements, declaredType.asElement(), false));
            }
        }
        return results;
    }

    @CheckForNull
    public static AnnotationMirror getAnnotationDirectOn(@NonNull Element element, String qName) {
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            if (toElement(annotationMirror.getAnnotationType()).getQualifiedName().toString().equals(qName)) {
                return annotationMirror;
            }
        }
        return null;
    }

    public static boolean appendMethodArg(PrintWriter writer, Consumer<PrintWriter> varWriter, boolean start, boolean breakLine, String indent, int indentNum) {
        if (breakLine) {
            if (start) {
                writer.println();
            } else {
                writer.println(",");
            }
            Utils.printIndent(writer, indent, indentNum);
        } else if (!start) {
            writer.print(", ");
        }
        varWriter.accept(writer);
        return false;
    }

    public static boolean filterPropertiesNeedRemoved(Property property, List<Pattern> includePatterns, String[] includes, List<Pattern> excludePatterns, String[] excludes, Set<String> extraExcludes) {
        return (includePatterns.stream().noneMatch(pattern -> pattern.matcher(property.getName()).matches())
                && Arrays.stream(includes).noneMatch(include -> include.equals(property.getName())))
                || excludePatterns.stream().anyMatch(pattern -> pattern.matcher(property.getName()).matches())
                || Arrays.stream(excludes).anyMatch(exclude -> exclude.equals(property.getName()))
                || extraExcludes.contains(property.getName());
    }
}
