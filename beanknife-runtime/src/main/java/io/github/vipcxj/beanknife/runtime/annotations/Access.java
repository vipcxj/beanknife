package io.github.vipcxj.beanknife.runtime.annotations;

public enum Access {
    PUBLIC, PRIVATE, PROTECTED, DEFAULT, NONE, UNKNOWN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static boolean canSeeFromSubClass(Access access) {
        return access != PRIVATE && access != NONE && access != UNKNOWN;
    }
}
