package io.github.vipcxj.beanknife.jpa;

import io.github.vipcxj.beanknife.core.models.*;
import io.github.vipcxj.beanknife.core.spi.ViewCodeGenerator;
import io.github.vipcxj.beanknife.core.utils.ParamInfo;
import io.github.vipcxj.beanknife.core.utils.Utils;
import io.github.vipcxj.beanknife.core.utils.VarMapper;
import org.apache.commons.text.StringEscapeUtils;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class JpaViewCodeGenerator implements ViewCodeGenerator {

    @Override
    public void ready(ViewContext context) {
        context.setContext(JpaContext.class.getName(), new JpaContext(context));
    }

    private void printSelectionType(PrintWriter writer, ViewContext context, Type type) {
        if (context.hasImport(JpaContext.TYPE_SELECTION)) {
            writer.print(JpaContext.SIMPLE_TYPE_SELECTION);
        } else {
            writer.print(JpaContext.TYPE_SELECTION);
        }
        writer.print("<");
        type.printType(writer, context, true, false);
        writer.print(">");
    }

    @Override
    public void print(PrintWriter writer, ViewContext context, String indent, int indentNum) {
        JpaContext jpaContext = context.getContext(JpaContext.class.getName());
        if (jpaContext.isEnabled()) {
            jpaContext.printConstructor(writer, indent);
            //printToSelection(writer, context, jpaContext, indent, indentNum);
        }
    }

/*
    private void printParameterPrefix(PrintWriter writer, boolean breakLine, String indent, int indentNum) {
        printParameterPrefix(writer, breakLine, false, indent, indentNum);
    }

    private void printParameterPrefix(PrintWriter writer, boolean breakLine, boolean start, String indent, int indentNum) {
        if (!start) {
            writer.print(",");
        }
        if (breakLine) {
            writer.println();
            Utils.printIndent(writer, indent, indentNum);
        } else if (!start) {
            writer.print(" ");
        }
    }

    private void printParameter(
            PrintWriter writer, Context context,
            Object key, VarMapper varMapper,
            boolean breakLine,
            boolean start,
            Type type, String name,
            String indent, int indentNum
    ) {
        printParameterPrefix(writer, breakLine, start, indent, indentNum);
        String varName = varMapper != null ? varMapper.getVar(key, name) : name;
        type.printType(writer, context, true, false);
        writer.print(" ");
        writer.print(varName);
    }

    private void printValueString(PrintWriter writer, JpaContext jpaContext, Context context, VarMapper varMapper, Property property) {
        if (jpaContext.isProvideSource()) {
            writer.print(property.getValueString("source"));
        } else {
            writer.print(varMapper.getVar(property, property.getName()));
        }
    }
*/

/*    private void printConstructor(PrintWriter writer, ViewContext context, JpaContext jpaContext, String indent, int indentNum) {
        VarMapper varMapper = jpaContext.getConstructorVarMapper();
        boolean breakLine = jpaContext.getArgsNum() > 6;
        Utils.printIndent(writer, indent, indentNum);
        writer.print("public ");
        writer.print(context.getGenType().getSimpleName());
        writer.print(" (");
        boolean start = true;
        if (jpaContext.isProvideSource()) {
            printParameter(
                    writer, context,
                    null, null, breakLine, true,
                    context.getTargetType(), "source",
                    indent, indentNum + 1
            );
            start = false;
        }
        for (Property property : jpaContext.getProperties()) {
            printParameter(
                    writer, context,
                    property, varMapper, breakLine, start,
                    property.getType(), property.getName(),
                    indent, indentNum + 1
            );
            if (start) {
                start = false;
            }
        }
        for (ParamInfo paramInfo : jpaContext.getParamInfos()) {
            Type type = Type.extract(context, paramInfo.getVar());
            printParameter(
                    writer, context,
                    paramInfo, varMapper, breakLine, start,
                    type, paramInfo.getExtraParamName(),
                    indent, indentNum + 1
            );
            if (start) {
                start = false;
            }
        }
        if (jpaContext.isFixConstructor()) {
            printParameter(
                    writer, context,
                    null, null, breakLine, start,
                    Type.create(context, "", "int", 0, false), jpaContext.getPreventConflictArgVar(),
                    indent, indentNum + 1
            );
        }
        if (breakLine) {
            writer.println();
            Utils.printIndent(writer, indent, indentNum);
        }
        writer.println(") {");
        for (Property property : context.getProperties()) {
            if (!property.isDynamic() && JpaContext.supportType(property.getType())) {
                Utils.printIndent(writer, indent, indentNum + 1);
                writer.print("this.");
                writer.print(context.getMappedFieldName(property));
                writer.print(" = ");
                if (property.isCustomMethod() && property.getExtractor() != null) {
                    ((StaticMethodExtractor) property.getExtractor()).print(writer, varMapper, jpaContext.isProvideSource(), indent, indentNum + 1);
                    writer.println(";");
                } else {
                    Property baseProperty = property.getBase();
                    if (baseProperty != null) {
                        Type converter = property.getConverter();
                        if (converter != null) {
                            writer.print("new ");
                            converter.printType(writer, context, false, false);
                            writer.print("().convert(");
                            printValueString(writer, jpaContext, context, varMapper, baseProperty);
                            writer.println(");");
                        } else if (property.isView()) {
                            writer.print(varMapper.getVar(property, property.getName()));
                            writer.println(";");
                        } else {
                            printValueString(writer, jpaContext, context, varMapper, baseProperty);
                            writer.println(";");
                        }
                    } else {
                        writer.print(varMapper.getVar(property, property.getName()));
                        writer.println(";");
                    }
                }
            }
        }
        Utils.printIndent(writer, indent, indentNum);
        writer.println("}");
        writer.println();
    }*/

//    private void printToSelection(PrintWriter writer, ViewContext context, JpaContext jpaContext, String indent, int indentNum) {
//        VarMapper varMapper = new VarMapper("cb", "from");
//        List<Property> extraProperties = context.getExtraProperties();
//        Map<String, ParamInfo> extraParams = context.getExtraParams();
//        boolean breakLine = extraProperties.size() + extraParams.size() > 3;
//        Utils.printIndent(writer, indent, indentNum);
//        writer.print("public static <T> ");
//        printSelectionType(writer, context, context.getGenType());
//        writer.print(" toJpaSelection(");
//        if (breakLine) {
//            writer.println();
//            Utils.printIndent(writer, indent, indentNum + 1);
//        }
//        if (context.hasImport(JpaContext.TYPE_CRITERIA_BUILDER)) {
//            writer.print(JpaContext.SIMPLE_TYPE_CRITERIA_BUILDER);
//        } else {
//            writer.print(JpaContext.TYPE_CRITERIA_BUILDER);
//        }
//        writer.print(" cb");
//        if (breakLine) {
//            writer.println(",");
//            Utils.printIndent(writer, indent, indentNum + 1);
//        } else {
//            writer.print(", ");
//        }
//        if (context.hasImport(JpaContext.TYPE_FROM)) {
//            writer.print(JpaContext.SIMPLE_TYPE_FROM);
//        } else {
//            writer.print(JpaContext.TYPE_FROM);
//        }
//        writer.print("<T, ");
//        context.getTargetType().printType(writer, context, true, false);
//        writer.print("> from");
//        for (Property property : jpaContext.getProperties()) {
//            if ((property.getConverter() == null && property.isView()) || !property.isBase()) {
//                String var = varMapper.getVar(property, property.getName());
//                printParameterPrefix(writer, breakLine, indent, indentNum + 1);
//                printSelectionType(writer, context, property.getType());
//                writer.print(" ");
//                writer.print(var);
//            }
//        }
//        for (ParamInfo paramInfo : jpaContext.getParamInfos()) {
//            String var = varMapper.getVar(paramInfo, paramInfo.getExtraParamName());
//            printParameterPrefix(writer, breakLine, indent, indentNum + 1);
//            Type type = Type.extract(context, paramInfo.getVar());
//            printSelectionType(writer, context, type);
//            writer.print(" ");
//            writer.print(var);
//        }
//        if (breakLine) {
//            writer.println();
//            Utils.printIndent(writer, indent, indentNum);
//        }
//        writer.println(") {");
//
//        Utils.printIndent(writer, indent, indentNum + 1);
//        writer.println("return cb.construct(");
//
//        Utils.printIndent(writer, indent, indentNum + 2);
//        context.getGenType().printType(writer, context, false, false);
//        writer.print(".class");
//        if (jpaContext.isProvideSource()) {
//            writer.println(",");
//            Utils.printIndent(writer, indent, indentNum + 2);
//            writer.print("from");
//        }
//        for (Property property : jpaContext.getProperties()) {
//            writer.println(",");
//            Utils.printIndent(writer, indent, indentNum + 2);
//            if (property.isBase()) {
//                writer.print("from.get(\"");
//                writer.print(StringEscapeUtils.escapeJava(property.getName()));
//                writer.print("\")");
//            } else {
//                writer.print(varMapper.getVar(property, property.getName()));
//            }
//        }
//        for (ParamInfo paramInfo : jpaContext.getParamInfos()) {
//            writer.println(",");
//            Utils.printIndent(writer, indent, indentNum + 2);
//            writer.print(varMapper.getVar(paramInfo, paramInfo.getExtraParamName()));
//        }
//        if (jpaContext.isFixConstructor()) {
//            writer.println(",");
//            Utils.printIndent(writer, indent, indentNum + 2);
//            writer.print("cb.literal(0)");
//        }
//
//        Utils.printIndent(writer, indent, indentNum + 1);
//        writer.println(");");
//
//        Utils.printIndent(writer, indent, indentNum);
//        writer.println("}");
//        writer.println();
//    }
}
