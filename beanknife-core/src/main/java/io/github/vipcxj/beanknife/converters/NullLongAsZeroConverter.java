package io.github.vipcxj.beanknife.converters;

import io.github.vipcxj.beanknife.PropertyConverter;

public class NullLongAsZeroConverter implements PropertyConverter<Long, Long> {
    @Override
    public Long convert(Long value) {
        return value != null ? value : 0L;
    }
}
