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
}
