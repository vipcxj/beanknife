package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.util.List;

public class MetaContext extends Context {

    private final Type baseType;

    public MetaContext(@Nonnull ProcessingEnvironment processingEnv, @Nonnull Type baseType, @Nonnull Type genType) {
        super(processingEnv, genType.getPackageName());
        this.baseType = baseType;
    }

    public List<Property> collectProperties(
            @Nonnull TypeElement element
    ) {
        Elements elementUtils = getProcessingEnv().getElementUtils();
        List<? extends Element> members = elementUtils.getAllMembers(element);
        for (Element member : members) {
            Property property = null;
            if (member.getKind() == ElementKind.FIELD) {
                property = Utils.createPropertyFromBase(this, null, (VariableElement) member, members, true);
            } else if (member.getKind() == ElementKind.METHOD) {
                property = Utils.createPropertyFromBase(this, null, (ExecutableElement) member, members, true);
            }
            if (property != null) {
                addProperty(property, true, false);
            }
        }
        return getProperties();
    }
}
