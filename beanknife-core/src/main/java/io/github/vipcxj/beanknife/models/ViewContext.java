package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.annotations.NewViewProperty;
import io.github.vipcxj.beanknife.annotations.OverrideViewProperty;
import io.github.vipcxj.beanknife.annotations.RemoveViewProperty;
import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ViewContext extends Context {

    private final ViewOfData viewOf;
    private final Type baseType;
    private final Type genType;
    private final boolean samePackage;
    private final List<String> errors;

    public ViewContext(@Nonnull ProcessingEnvironment processingEnv, @Nonnull Type baseType, @Nonnull Type genType, @Nonnull ViewOfData viewOf) {
        super(processingEnv, genType.getPackageName());
        this.viewOf = viewOf;
        this.baseType = baseType;
        this.genType = genType;
        this.samePackage = baseType.isSamePackage(genType);
        this.errors = new ArrayList<>();
    }

    public ViewOfData getViewOf() {
        return viewOf;
    }

    private int checkAnnConflict(NewViewProperty newViewProperty, OverrideViewProperty overrideViewProperty) {
        return (newViewProperty != null ? 1 : 0)
                + (overrideViewProperty != null ? 1 : 0);
    }

    private void error(String message) {
        errors.add(message);
        Utils.logWarn(getProcessingEnv(), message);
    }

    public List<Property> collectProperties(
            @Nonnull TypeElement baseType,
            @Nullable TypeElement configType,

    ) {
        Elements elementUtils = getProcessingEnv().getElementUtils();
        List<? extends Element> members = elementUtils.getAllMembers(baseType);
        for (Element member : members) {
            Property property = null;
            if (member.getKind() == ElementKind.FIELD) {
                property = Utils.createPropertyFromBase(this, viewOf, (VariableElement) member, members, samePackage);
            } else if (member.getKind() == ElementKind.METHOD) {
                property = Utils.createPropertyFromBase(this, viewOf, (ExecutableElement) member, members, samePackage);
            }
            if (property != null) {
                addProperty(property, true, false);
            }
        }
        List<Pattern> includePatterns = Arrays.stream(viewOf.getIncludePattern().split(",\\s")).map(Pattern::compile).collect(Collectors.toList());
        List<Pattern> excludePatterns = Arrays.stream(viewOf.getExcludePattern().split(",\\s")).map(Pattern::compile).collect(Collectors.toList());
        getProperties().removeIf(property -> (includePatterns.stream().noneMatch(pattern -> pattern.matcher(property.getName()).matches())
                && Arrays.stream(viewOf.getIncludes()).noneMatch(include -> include.equals(property.getName())))
                || excludePatterns.stream().anyMatch(pattern -> pattern.matcher(property.getName()).matches())
                || Arrays.stream(viewOf.getExcludes()).anyMatch(exclude -> exclude.equals(property.getName())));
        if (configType != null) {
            members = elementUtils.getAllMembers(configType);
            for (Element member : members) {
                NewViewProperty newViewProperty = member.getAnnotation(NewViewProperty.class);
                OverrideViewProperty overrideViewProperty = member.getAnnotation(OverrideViewProperty.class);
                int exists = checkAnnConflict(newViewProperty, overrideViewProperty);
                if (exists > 1) {
                    error("NewViewProperty and OverrideViewProperty should not be put on the same property.");
                    continue;
                }
                if (exists == 0) {
                    continue;
                }
                if (newViewProperty != null) {
                    if (getProperties().stream().anyMatch(p -> p.getName().equals(newViewProperty.value()))) {
                        error("The property " + newViewProperty.value() + " already exists, so the @NewViewProperty annotation is invalid and has been ignored.");
                        continue;
                    }
                }
                if (overrideViewProperty != null) {
                    if (getProperties().stream().noneMatch(p -> p.getName().equals(overrideViewProperty.value()))) {
                        error("The property " + overrideViewProperty.value() + " does not exists, so the @OverrideViewProperty annotation is invalid and has been ignored.");
                        continue;
                    }
                }
                if (member.getKind() == ElementKind.FIELD) {

                } else if (member.getKind() == ElementKind.METHOD) {

                }
            }

        }
    }
}
