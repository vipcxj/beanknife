package io.github.vipcxj.beanknife.jpa;

import io.github.vipcxj.beanknife.core.models.ViewContext;
import io.github.vipcxj.beanknife.core.spi.ViewCodeGenerator;

import java.io.PrintWriter;

public class JpaViewCodeGenerator implements ViewCodeGenerator {

    @Override
    public void ready(ViewContext context) {
        context.setContext(JpaContext.class.getName(), new JpaContext(context));
    }

    @Override
    public void print(PrintWriter writer, ViewContext context, String indent, int indentNum) {
        JpaContext jpaContext = context.getContext(JpaContext.class.getName());
        if (jpaContext.isEnabled()) {
            jpaContext.printConstructor(writer, indent, indentNum);
            jpaContext.printSelectionMethod(writer, indent, indentNum);
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
