package io.github.vipcxj.beanknife;

import javax.annotation.Nonnull;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import java.util.regex.Matcher;

public class ClassName {
    private final String packageName;
    private final String simpleName;
    private final String className;

    /**
     * 构造函数
     * @param packageName 包名
     * @param simpleName 类名，不包括包名，特别的，对于嵌套类，有如下形式Parent.Nest1.Nest2
     */
    public ClassName(@Nonnull String packageName, @Nonnull String simpleName) {
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.className = combineName(packageName, simpleName);
    }

    @Nonnull
    public static ClassName extract(ProcessingEnvironment environment, TypeElement element) {
        String packageName = environment.getElementUtils().getPackageOf(element).getQualifiedName().toString();
        String qualifiedName = element.getQualifiedName().toString();
        return new ClassName(packageName, packageName.isEmpty() ? qualifiedName : qualifiedName.substring(packageName.length() + 1));
    }

    /**
     * 获取包名
     * @return 包名
     */
    @Nonnull
    public String getPackageName() {
        return packageName;
    }

    /**
     * 获取类名，不包括包名，特别的，对于嵌套类，有如下形式Parent.Nest1.Nest2
     * @return 类名
     */
    @Nonnull
    public String getSimpleName() {
        return simpleName;
    }

    /**
     * 获取用于Import的类名，不包括包名。特别的对于嵌套类，比如 a.b.C.D, 返回C
     * @return 用于Import的类名
     */
    @Nonnull
    public String getImportSimpleName() {
        int index = simpleName.indexOf('.');
        if (index != -1) {
            return simpleName.substring(0, index);
        } else {
            return simpleName;
        }
    }

    /**
     * 获取单独类名，若不是嵌套类，则为不包括包名的类名。对于嵌套类，列如 a.b.c.D.E, 返回 D$E
     * @return 单独类名
     */
    @Nonnull
    public String getFlatSimpleName() {
        return simpleName.replaceAll("\\.", Matcher.quoteReplacement("$"));
    }

    /**
     * 获取用于import的全限定名
     * @return 用于import的全限定名
     */
    @Nonnull
    public String getImportName() {
        return combineName(packageName, getImportSimpleName());
    }

    private static String combineName(String packageName, @Nonnull String simpleClassName) {
        return packageName.isEmpty() ? simpleClassName : packageName + "." + simpleClassName;
    }

    /**
     * 获取全限定类名。特别的，对于嵌套类，例如 a.b.c.D.E 返回 a.b.c.D.E
     * @return 全限定类名
     */
    @Nonnull
    public String getQualifiedClassName() {
        return className;
    }

    /**
     * 获取全限定单独类名。特别的，对于嵌套类，例如 a.b.c.D.E 返回 a.b.c.D$E
     * @return 全限定单独类名
     */
    @Nonnull
    public String getFlatQualifiedClassName() {
        return combineName(packageName, getFlatSimpleName());
    }

}
