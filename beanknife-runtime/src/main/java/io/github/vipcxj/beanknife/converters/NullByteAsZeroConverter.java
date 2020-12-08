package io.github.vipcxj.beanknife.converters;

import io.github.vipcxj.beanknife.PropertyConverter;

public class NullByteAsZeroConverter implements PropertyConverter<Byte, Byte> {
    @Override
    public Byte convert(Byte value) {
        return value != null ? value : 0;
    }
}
