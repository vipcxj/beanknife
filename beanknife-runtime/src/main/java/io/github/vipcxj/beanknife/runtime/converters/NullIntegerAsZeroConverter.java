package io.github.vipcxj.beanknife.runtime.converters;

import io.github.vipcxj.beanknife.runtime.PropertyConverter;

public class NullIntegerAsZeroConverter implements PropertyConverter<Integer, Integer> {
    @Override
    public Integer convert(Integer value) {
        return value != null ? value : 0;
    }

    @Override
    public Integer convertBack(Integer value) {
        return value;
    }
}
