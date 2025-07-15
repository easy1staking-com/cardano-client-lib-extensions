package com.easy1staking.math;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@Slf4j
public class BigIntegerUtil {

    public static BigInteger divideCeiling(BigInteger num, BigInteger den) {
        return new BigDecimal(num).divide(new BigDecimal(den), RoundingMode.CEILING).toBigInteger();
    }

}
