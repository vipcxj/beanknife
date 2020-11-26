package io.github.vipcxj.beanknife;

import javax.annotation.Nonnull;

public class ClassName {
    private final String packageName;
    private final String simpleClassName;

    public ClassName(@Nonnull String packageName, @Nonnull String simpleClassName) {
        this.packageName = packageName;
        this.simpleClassName = simpleClassName;
    }

    @Nonnull
    public static ClassName parse(@Nonnull String className) {
        String packageName = "";
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }
        String simpleClassName = className.substring(lastDot + 1);
        return new ClassName(packageName, simpleClassName);
    }

    @Nonnull
    public String getPackageName() {
        return packageName;
    }

    @Nonnull
    public String getSimpleClassName() {
        return simpleClassName;
    }

    @Nonnull
    public String getClassName() {
        return !packageName.isEmpty() ? packageName + "." + simpleClassName : simpleClassName;
    }
}
