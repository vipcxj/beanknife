package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.utils.Utils;

import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MetaContext extends Context {

    private final Type genType;

    public MetaContext(@Nonnull ProcessingEnvironment processingEnv, @Nonnull Type genType) {
        super(processingEnv, genType.getPackageName());
        this.genType = genType;
    }

    public void collectData(@Nonnull TypeElement element) {
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
                addProperty(property, false);
            }
        }
        getProperties().removeIf(property -> !Utils.canSeeFromOtherClass(property, true));
    }

    @Override
    public boolean print(@Nonnull PrintWriter writer) {
        if (super.print(writer)) {
            writer.println();
        }
        genType.openClass(writer, Modifier.PUBLIC, this, INDENT, 0);
        Set<String> names = new HashSet<>();
        for (Property property : getProperties()) {
            String variableName = Utils.createValidFieldName(property.getName(), names);
            names.add(variableName);
            Utils.printIndent(writer, INDENT, 1);
            writer.print("public static final String ");
            writer.print(variableName);
            writer.print(" = \"");
            writer.print(property.getName());
            writer.print("\";");
            writer.println();
        }
        genType.closeClass(writer, INDENT, 0);
        return true;
    }
}
