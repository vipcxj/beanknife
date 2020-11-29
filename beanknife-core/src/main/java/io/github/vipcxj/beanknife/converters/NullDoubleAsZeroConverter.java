package io.github.vipcxj.beanknife.converters;

import io.github.vipcxj.beanknife.PropertyConverter;

public class NullDoubleAsZeroConverter implements PropertyConverter<Double, Double> {
    @Override
    public Double convert(Double value) {
        return value != null ? value : 0.0;
    }
}
