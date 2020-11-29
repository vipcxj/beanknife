package io.github.vipcxj.beanknife.converters;

import io.github.vipcxj.beanknife.PropertyConverter;

public class NullFloatAsZeroConverter implements PropertyConverter<Float, Float> {
    @Override
    public Float convert(Float value) {
        return value != null ? value : 0.0f;
    }
}
