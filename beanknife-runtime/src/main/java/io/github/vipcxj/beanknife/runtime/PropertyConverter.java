package io.github.vipcxj.beanknife.runtime;

public interface PropertyConverter<FromType, ToType> {
    ToType convert(FromType value);
}
