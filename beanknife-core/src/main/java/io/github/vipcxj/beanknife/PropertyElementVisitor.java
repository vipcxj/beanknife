package io.github.vipcxj.beanknife;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner9;
import java.util.ArrayList;
import java.util.List;

public class PropertyElementVisitor<P> extends ElementScanner9<List<Property>, P> {

    private final List<Property> properties = new ArrayList<>();

    @Override
    public List<Property> visitVariable(VariableElement e, P p) {
        super.visitVariable(e, p);
        if (e.getKind().isField()) {
            Property property = PropertyElementHelper.createProperty(e);
            if (property != null && property.getModifier() != Modifier.PRIVATE) {
                PropertyElementHelper.addProperty(properties, property, false);
            }
        }
        return properties;
    }

    @Override
    public List<Property> visitExecutable(ExecutableElement e, P p) {
        super.visitExecutable(e, p);
        if (e.getKind() == ElementKind.METHOD) {
            Property property = PropertyElementHelper.createProperty(e);
            if (property != null && property.getModifier() != Modifier.PRIVATE) {
                PropertyElementHelper.addProperty(properties, property, false);
            }
        }
        return properties;
    }
}
