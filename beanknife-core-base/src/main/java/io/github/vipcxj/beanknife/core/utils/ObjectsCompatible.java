package io.github.vipcxj.beanknife.core.utils;

import java.util.function.Supplier;

public class ObjectsCompatible {
    /**
     * Returns the first argument if it is non-{@code null} and otherwise
     * returns the non-{@code null} value of {@code supplier.get()}.
     *
     * @param obj an object
     * @param supplier of a non-{@code null} object to return if the first argument
     *                 is {@code null}
     * @param <T> the type of the first argument and return type
     * @return the first argument if it is non-{@code null} and otherwise
     *         the value from {@code supplier.get()} if it is non-{@code null}
     * @throws NullPointerException if both {@code obj} is null and
     *        either the {@code supplier} is {@code null} or
     *        the {@code supplier.get()} value is {@code null}
     * @since 9
     */
    public static <T> T requireNonNullElseGet(T obj, Supplier<? extends T> supplier) {
        return (obj != null) ? obj
                : java.util.Objects.requireNonNull(java.util.Objects.requireNonNull(supplier, "supplier").get(), "supplier.get()");
    }
}
