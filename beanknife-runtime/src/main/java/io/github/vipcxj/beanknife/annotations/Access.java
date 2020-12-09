package io.github.vipcxj.beanknife.annotations;

import javax.lang.model.element.Modifier;

public enum Access {
    PUBLIC, PRIVATE, PROTECTED, DEFAULT, NONE, UNKNOWN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public Modifier toModifier() {
        switch (this) {
            case PUBLIC:
                return Modifier.PUBLIC;
            case DEFAULT:
                return Modifier.DEFAULT;
            case PROTECTED:
                return Modifier.PROTECTED;
            case PRIVATE:
            case UNKNOWN:
            case NONE:
                return Modifier.PRIVATE;
            default:
                throw new IllegalStateException("This is impossible!");
        }
    }

    public static Access fromModifier(Modifier modifier) {
        if (modifier == null) {
            return Access.NONE;
        }
        switch (modifier) {
            case PUBLIC:
                return Access.PUBLIC;
            case DEFAULT:
                return Access.DEFAULT;
            case PROTECTED:
                return Access.PROTECTED;
            case PRIVATE:
                return Access.PRIVATE;
            default:
                return Access.UNKNOWN;
        }
    }
}
