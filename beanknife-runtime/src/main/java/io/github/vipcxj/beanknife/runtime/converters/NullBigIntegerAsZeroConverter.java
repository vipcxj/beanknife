package io.github.vipcxj.beanknife.runtime.converters;

import io.github.vipcxj.beanknife.runtime.PropertyConverter;

import java.math.BigInteger;

public class NullBigIntegerAsZeroConverter implements PropertyConverter<BigInteger, BigInteger> {
    @Override
    public BigInteger convert(BigInteger value) {
        return value != null ? value : BigInteger.ZERO;
    }

    @Override
    public BigInteger convertBack(BigInteger value) {
        return value;
    }
}
