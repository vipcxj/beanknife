package io.github.vipcxj.beanknife.core.models;

import com.sun.source.util.Trees;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.runtime.annotations.internal.GeneratedMeta;
import io.github.vipcxj.beanknife.core.utils.Utils;
import org.apache.commons.text.StringEscapeUtils;

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

    public MetaContext(@NonNull Trees trees, @NonNull ProcessingEnvironment processingEnv, @NonNull ViewMetaData viewMeta, @NonNull List<ViewOfData> viewOfDataList) {
        super(trees, processingEnv, null);
        this.viewMeta = viewMeta;
        this.viewOfDataList = viewOfDataList;
        TypeElement targetElement = viewMeta.getOf();
        this.genType = Utils.extractGenType(
                Type.extract(this, targetElement),
                viewMeta.getValue(),
                viewMeta.getPackageName(),
                "Meta"
        ).withoutParameters();
        this.packageName = this.genType.getPackageName();
        this.containers.push(Type.fromPackage(this, this.packageName));
        this.generatedType = Type.extract(this, GeneratedMeta.class);
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
        importVariable(Type.extract(this, viewMeta.getOf()));
        importVariable(Type.extract(this, viewMeta.getConfig()));
        if (!viewOfDataList.isEmpty()) {
            for (ViewOfData viewOfData : viewOfDataList) {
                importVariable(Type.extract(this, viewOfData.getConfigElement()));
            }
        }
    }

    @Override
    public boolean print(@NonNull PrintWriter writer) {
        if (super.print(writer)) {
            writer.println();
        }
        writer.print("@");
        generatedType.printType(writer, this, false, false);
        int viewOfNum = viewOfDataList.size();
        if (viewOfNum > 0) {
            writer.println("(");
            Utils.printIndent(writer, INDENT, 1);
            writer.print("targetClass = ");
            Type.extract(this, viewMeta.getOf()).printType(writer, this, false, false);
            writer.println(".class,");
            Utils.printIndent(writer, INDENT, 1);
            writer.print("configClass = ");
            Type.extract(this, viewMeta.getConfig()).printType(writer, this, false, false);
            writer.println(".class,");
            Utils.printIndent(writer, INDENT, 1);
            writer.println("proxies = {");
            int i = 0;
            for (ViewOfData viewOfData : viewOfDataList) {
                Utils.printIndent(writer, INDENT, 2);
                Type configType = Type.extract(this, viewOfData.getConfigElement());
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
            writer.print("(targetClass = ");
            Type.extract(this, viewMeta.getOf()).printType(writer, this, false, false);
            writer.print(".class, ");
            writer.print("configClass = ");
            Type.extract(this, viewMeta.getConfig()).printType(writer, this, false, false);
            writer.print(".class");
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
            writer.print(StringEscapeUtils.escapeJava(property.getName()));
            writer.print("\";");
            writer.println();
        }
        if (viewOfNum > 0) {
            writer.println();
            Utils.printIndent(writer, INDENT, 1);
            writer.println("public static class Views {");
            names.clear();
            viewOfDataList.stream()
                    .map(viewOfData -> Utils.extractGenType(
                            Type.extract(this, viewOfData.getTargetElement()),
                            viewOfData.getGenName(),
                            viewOfData.getGenPackage(),
                            "View"
                    ).getQualifiedName())
                    .forEach(viewName -> {
                        String variableName = Utils.createValidFieldName(viewName, names);
                        names.add(variableName);
                        Utils.printIndent(writer, INDENT, 2);
                        writer.print("public static final String ");
                        writer.print(variableName);
                        writer.print(" = \"");
                        writer.print(StringEscapeUtils.escapeJava(viewName));
                        writer.print("\";");
                        writer.println();
                    });
            Utils.printIndent(writer, INDENT, 1);
            writer.println("}");
        }
        genType.closeClass(writer, INDENT, 0);
        return true;
    }
}
