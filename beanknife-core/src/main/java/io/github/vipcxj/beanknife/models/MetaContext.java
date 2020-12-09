package io.github.vipcxj.beanknife.models;

import io.github.vipcxj.beanknife.annotations.GeneratedMeta;
import io.github.vipcxj.beanknife.utils.Utils;
import org.apache.commons.text.StringEscapeUtils;

import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MetaContext extends Context {

    private final ViewMetaData viewMeta;
    private final List<ViewOfData> viewOfDataList;
    private final Type genType;
    private final Type generatedType;

    public MetaContext(@Nonnull ProcessingEnvironment processingEnv, @Nonnull ViewMetaData viewMeta, @Nonnull List<ViewOfData> viewOfDataList) {
        super(processingEnv);
        this.viewMeta = viewMeta;
        this.viewOfDataList = viewOfDataList;
        TypeElement targetElement = viewMeta.getOf();
        this.genType = Utils.extractGenType(
                Type.extract(targetElement.asType()),
                viewMeta.getValue(),
                viewMeta.getPackageName(),
                "Meta"
        ).withoutParameters();
        this.packageName = this.genType.getPackageName();
        this.containers.push(Type.fromPackage(this.packageName));
        this.generatedType = Type.extract(processingEnv.getElementUtils().getTypeElement(GeneratedMeta.class.getCanonicalName()).asType());
    }

    public ViewMetaData getViewMeta() {
        return viewMeta;
    }

    public Type getGenType() {
        return genType;
    }

    public void collectData() {
        TypeElement element = viewMeta.getOf();
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
        importAll();
    }

    private void importAll() {
        importVariable(generatedType);
        if (!viewOfDataList.isEmpty()) {
            for (ViewOfData viewOfData : viewOfDataList) {
                importVariable(Type.extract(viewOfData.getConfigElement().asType()));
            }
        }
    }

    @Override
    public boolean print(@Nonnull PrintWriter writer) {
        if (super.print(writer)) {
            writer.println();
        }
        writer.print("@");
        generatedType.printType(writer, this, false, false);
        writer.println("(");
        Utils.printIndent(writer, INDENT, 1);
        writer.print("targetClass = \"");
        writer.print(StringEscapeUtils.escapeJava(viewMeta.getOf().getQualifiedName().toString()));
        writer.println("\",");
        Utils.printIndent(writer, INDENT, 1);
        writer.print("configClass = \"");
        writer.print(StringEscapeUtils.escapeJava(viewMeta.getConfig().getQualifiedName().toString()));
        int viewOfNum = viewOfDataList.size();
        if (viewOfNum > 0) {
            writer.println("\",");
            Utils.printIndent(writer, INDENT, 1);
            writer.println("proxies = {");
            int i = 0;
            for (ViewOfData viewOfData : viewOfDataList) {
                Utils.printIndent(writer, INDENT, 2);
                Type configType = Type.extract(viewOfData.getConfigElement().asType());
                configType.printType(writer, this, false, false);
                writer.print(".class");
                if (i++ != viewOfNum - 1) {
                    writer.println(",");
                } else {
                    writer.println();
                }
            }
            Utils.printIndent(writer, INDENT, 1);
            writer.println("}");
        } else {
            writer.println("\"");
        }
        writer.println(")");
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
