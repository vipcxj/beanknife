package io.github.vipcxj.beanknife.runtime.converters;

import io.github.vipcxj.beanknife.runtime.PropertyConverter;

public class NullLongAsZeroConverter implements PropertyConverter<Long, Long> {
    @Override
    public Long convert(Long value) {
        return value != null ? value : 0L;
    }

    @Override
    public Long convertBack(Long value) {
        return value;
    }
}
