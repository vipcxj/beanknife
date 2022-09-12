package io.github.vipcxj.beanknife.core.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameMapperHelper {

    private static final Pattern PT_VARIABLE = Pattern.compile("\\$\\{(?<var>[A-Za-z0-9-_]+)((%%(?<longBackToRemove>.+))|(%(?<shortBackToRemove>.+))|(##(?<longFrontToRemove>.+))|(#(?<shortFrontToRemove>.+)))?}");

    public static String parameterSubstitution(String input, Map<String, String> vars) {
        Matcher matcher = PT_VARIABLE.matcher(input);
        //noinspection StringBufferMayBeStringBuilder
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String var = matcher.group("var");
            String varValue = vars.get(var);
            if (varValue == null) {
                return input + "__NoSuchVar_" + var;
            }
            String shortBackToRemove = matcher.group("shortBackToRemove");
            String longBackToRemove = matcher.group("longBackToRemove");
            String shortFrontToRemove = matcher.group("shortFrontToRemove");
            String longFrontToRemove = matcher.group("longFrontToRemove");
            if (shortBackToRemove != null) {
                String pt = StringUtils.convertGlobToRegex(shortBackToRemove, false) + "$";
                Matcher m = Pattern.compile(pt).matcher(varValue);
                int offset = 0;
                int find = -1;
                while (m.find(offset)) {
                    find = m.start();
                    offset = find + 1;
                    if (offset >= varValue.length()) {
                        break;
                    }
                }
                if (find >= 0) {
                    varValue = varValue.substring(0, find);
                }
            } else if (longBackToRemove != null) {
                String pt = StringUtils.convertGlobToRegex(longBackToRemove, true) + "$";
                varValue = varValue.replaceFirst(pt, "");
            } else if (shortFrontToRemove != null) {
                String pt = "^" + StringUtils.convertGlobToRegex(shortFrontToRemove, false);
                varValue = varValue.replaceFirst(pt, "");
            } else if (longFrontToRemove != null) {
                String pt = "^" + StringUtils.convertGlobToRegex(longFrontToRemove, true);
                varValue = varValue.replaceFirst(pt, "");
            }
            matcher.appendReplacement(sb, varValue);
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
