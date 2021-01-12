package io.github.vipcxj.beanknife.runtime.converters;

import io.github.vipcxj.beanknife.runtime.PropertyConverter;

public class NullStringAsEmptyConverter implements PropertyConverter<String, String> {
    @Override
    public String convert(String value) {
        return value != null ? value : "";
    }

    @Override
    public String convertBack(String value) {
        return value;
    }
}
