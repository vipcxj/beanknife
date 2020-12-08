package io.github.vipcxj.beanknife.converters;

import io.github.vipcxj.beanknife.PropertyConverter;

public class NullStringAsEmptyConverter implements PropertyConverter<String, String> {
    @Override
    public String convert(String value) {
        return value != null ? value : "";
    }
}
