package io.github.vipcxj.beanknife.runtime.utils;

import java.lang.annotation.ElementType;

public enum AnnotationPos {
    SAME,
    TYPE,
    FIELD,
    GETTER,
    SETTER;

    public ElementType toElementType() {
        switch (this) {
            case TYPE:
                return ElementType.TYPE;
            case FIELD:
                return ElementType.FIELD;
            case GETTER:
            case SETTER:
                return ElementType.METHOD;
            default:
                throw new UnsupportedOperationException();
        }
    }
}
