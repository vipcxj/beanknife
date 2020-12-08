package io.github.vipcxj.beanknife;

public interface PropertyConverter<FromType, ToType> {
    ToType convert(FromType value);
}
