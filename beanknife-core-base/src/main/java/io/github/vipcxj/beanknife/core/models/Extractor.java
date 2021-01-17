package io.github.vipcxj.beanknife.core.models;

import edu.umd.cs.findbugs.annotations.CheckForNull;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.core.utils.VarMapper;
import io.github.vipcxj.beanknife.runtime.utils.CacheType;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.io.PrintWriter;

public interface Extractor {

    boolean check();
    @NonNull
    ViewContext getContext();
    @NonNull
    Type getReturnType();
    @CheckForNull
    Type getContainer();
    @NonNull
    ExecutableElement getExecutableElement();
    boolean isDynamic();
    default boolean useBeanProvider() {
        return !getExecutableElement().getModifiers().contains(Modifier.STATIC);
    }
    default void printConfigBean(PrintWriter writer, @NonNull String requester) {
        if (getContainer() == null) {
            throw new IllegalStateException("This is impossible!");
        }
        if (getExecutableElement().getModifiers().contains(Modifier.STATIC)) {
            getContainer().printType(writer, getContext(), false, false);
        } else {
            CacheType cacheType = getContext().getViewOf().getConfigureBeanCacheType();
            if (isDynamic()) {
                if (cacheType == CacheType.LOCAL) {
                    writer.print("this.");
                    writer.print(getContext().getConfigureBeanGetterVar());
                    writer.print("()");
                } else {
                    getContext().printInitConfigureBean(writer, requester, false);
                }
            } else {
                getContext().printInitConfigureBean(writer, requester, true);
            }
        }
    }
}
