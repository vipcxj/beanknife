package io.github.vipcxj.beanknife.runtime.converters;

import io.github.vipcxj.beanknife.runtime.PropertyConverter;

public class NullFloatAsZeroConverter implements PropertyConverter<Float, Float> {
    @Override
    public Float convert(Float value) {
        return value != null ? value : 0.0f;
    }

    @Override
    public Float convertBack(Float value) {
        return value;
    }
}
