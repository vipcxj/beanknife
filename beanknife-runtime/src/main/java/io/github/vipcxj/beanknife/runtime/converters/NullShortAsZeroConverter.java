package io.github.vipcxj.beanknife.runtime.converters;

import io.github.vipcxj.beanknife.runtime.PropertyConverter;

public class NullShortAsZeroConverter implements PropertyConverter<Short, Short> {
    @Override
    public Short convert(Short value) {
        return value != null ? value : 0;
    }
}
