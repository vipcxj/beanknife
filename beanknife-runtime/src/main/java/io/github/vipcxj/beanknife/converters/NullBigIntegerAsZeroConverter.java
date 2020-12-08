package io.github.vipcxj.beanknife.converters;

import io.github.vipcxj.beanknife.PropertyConverter;

import java.math.BigInteger;

public class NullBigIntegerAsZeroConverter implements PropertyConverter<BigInteger, BigInteger> {
    @Override
    public BigInteger convert(BigInteger value) {
        return value != null ? value : BigInteger.ZERO;
    }
}
