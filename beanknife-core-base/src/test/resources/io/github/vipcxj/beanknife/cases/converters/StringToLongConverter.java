package io.github.vipcxj.beanknife.cases.converters;

import io.github.vipcxj.beanknife.runtime.PropertyConverter;

public class StringToLongConverter implements PropertyConverter<String, Long> {
    @Override
    public Long convert(String value) {
        return value != null ? Long.parseLong(value) : null;
    }

    @Override
    public String convertBack(Long value) {
        return value != null ? value.toString() : null;
    }
}
