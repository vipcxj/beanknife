package io.github.vipcxj.beanknife.core.utils;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.List;

public class ElementsCompatible {

    public static boolean isEqualType(TypeElement thisType, TypeElement targetType) {
        return thisType.getQualifiedName().contentEquals(targetType.getQualifiedName());
    }

    public static boolean isAssignableFrom(TypeElement thisType, TypeElement targetType, boolean includeInterface) {
        if (isEqualType(thisType, targetType)) {
            return true;
        }
        if (Utils.isObjectType(targetType)) {
            return Utils.isObjectType(thisType);
        }
        if (thisType.getKind() == ElementKind.INTERFACE && !includeInterface) {
            return false;
        }
        TypeMirror superClass = targetType.getSuperclass();
        if (superClass.getKind() == TypeKind.NONE && !includeInterface) {
            return false;
        }
        if (superClass.getKind() != TypeKind.NONE) {
            TypeElement superElement = Utils.toElement((DeclaredType) superClass);
            boolean result = isAssignableFrom(thisType, superElement, includeInterface);
            if (result) {
                return true;
            }
        }
        if (includeInterface) {
            for (TypeMirror anInterface : targetType.getInterfaces()) {
                TypeElement interfaceElement = Utils.toElement((DeclaredType) anInterface);
                boolean result = isAssignableFrom(thisType, interfaceElement, true);
                if (result) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean fieldHides(Element hider, Element hidden) {
        if (hider.getKind() != ElementKind.FIELD || hidden.getKind() != ElementKind.FIELD) {
            return false;
        }
        if (!hider.getSimpleName().contentEquals(hidden.getSimpleName())) {
            return false;
        }
        TypeElement hiderOwner = (TypeElement) hider.getEnclosingElement();
        TypeElement hiddenOwner = (TypeElement) hidden.getEnclosingElement();
        return isAssignableFrom(hiddenOwner, hiderOwner, false);
    }

    public static List<Element> getAllMembers(Elements elements, TypeElement element) {
        List<? extends Element> members = elements.getAllMembers(element);
        TypeMirror superType = element.getSuperclass();
        if (superType.getKind() != TypeKind.NONE) {
            List<Element> result = new ArrayList<>(LombokUtils.getAllLombokFields(Utils.toElement((DeclaredType) superType)));
            result.addAll(members);
            return result;
        } else {
            return new ArrayList<>(members);
        }
    }

    @CheckForNull
    public static PackageElement getPackageOf(@NonNull Element element) {
        if (element.getKind() == ElementKind.PACKAGE) {
            return (PackageElement) element;
        }
        Element enclosingElement = element.getEnclosingElement();
        if (enclosingElement == null) {
            return null;
        }
        return getPackageOf(enclosingElement);
    }

    @CheckForNull
    public static String getPackageNameOf(@NonNull Element element) {
        PackageElement packageElement = getPackageOf(element);
        return packageElement != null ? packageElement.getQualifiedName().toString() : null;
    }
}
