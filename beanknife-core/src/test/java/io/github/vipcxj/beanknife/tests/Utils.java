package io.github.vipcxj.beanknife.tests;

public class Utils {

    public static String getPath(Class<?> clazz) {
        String canonicalName = clazz.getCanonicalName();
        if (canonicalName == null) {
            return null;
        }
        return "/" + canonicalName.replaceAll("\\.", "/") + ".java";
    }
}
