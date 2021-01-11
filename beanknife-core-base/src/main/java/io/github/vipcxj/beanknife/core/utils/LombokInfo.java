package io.github.vipcxj.beanknife.core.utils;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.github.vipcxj.beanknife.runtime.annotations.Access;

import javax.lang.model.element.Element;

public class LombokInfo {
    private final Access lombokGetter;
    private final Access lombokSetter;

    public LombokInfo(@NonNull Element element, @NonNull Access typeGetterAccess, @NonNull Access typeSetterAccess) {
        this.lombokGetter = LombokUtils.getGetterAccess(element, typeGetterAccess);
        this.lombokSetter = LombokUtils.getSetterAccess(element, typeSetterAccess);
    }

    public boolean isWritable(boolean samePackage) {
        if (samePackage) {
            return lombokSetter != Access.NONE && lombokSetter != Access.PRIVATE;
        } else {
            return lombokSetter == Access.PUBLIC;
        }
    }

    public boolean isReadable(boolean samePackage) {
        if (samePackage) {
            return lombokGetter != Access.NONE && lombokGetter != Access.PRIVATE;
        } else {
            return lombokGetter == Access.PUBLIC;
        }
    }

    public boolean hasGetter() {
        return lombokGetter != Access.NONE;
    }
}
