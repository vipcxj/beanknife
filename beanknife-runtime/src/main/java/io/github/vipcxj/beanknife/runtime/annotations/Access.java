package io.github.vipcxj.beanknife.runtime.annotations;

public enum Access {
    PUBLIC, PRIVATE, PROTECTED, DEFAULT, NONE, UNKNOWN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
