package io.github.vipcxj.beanknife.annotations;

public enum Access {
    PUBLIC, PRIVATE, PROTECTED, DEFAULT, NONE, UNKNOWN;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
