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
}
