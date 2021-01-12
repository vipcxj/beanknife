package io.github.vipcxj.beanknife.runtime.converters;

import io.github.vipcxj.beanknife.runtime.PropertyConverter;

public class NullByteAsZeroConverter implements PropertyConverter<Byte, Byte> {
    @Override
    public Byte convert(Byte value) {
        return value != null ? value : 0;
    }

    @Override
    public Byte convertBack(Byte value) {
        return value;
    }
}
