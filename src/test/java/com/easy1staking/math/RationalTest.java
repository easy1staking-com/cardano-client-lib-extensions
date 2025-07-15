package com.easy1staking.math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

public class RationalTest {

    @Test
    public void testGt() {
        Assertions.assertTrue(Rational.ONE.gt(Rational.ZERO));
    }

    @Test
    public void testGt1() {
        Assertions.assertFalse(Rational.from(102L, 100L).gt(Rational.from(10L)));
    }

    @Test
    public void testGt2() {
        Assertions.assertTrue(Rational.from(11L, 10L).gt(Rational.ONE));
    }

    @Test
    public void testLt() {
        Assertions.assertTrue(Rational.ZERO.lt(Rational.ONE));
    }

    @Test
    public void testLtNeg() {
        Assertions.assertTrue(Rational.ONE.negate().lt(Rational.ONE));
    }

    @Test
    public void testNormalization() {
        Rational r = Rational.from(2L, 4L);
        Assertions.assertEquals(Rational.from(1L, 2L), r);
    }

    @Test
    public void testNegativeDenominator() {
        Rational r = Rational.from(1L, -2L);
        Assertions.assertEquals(BigInteger.valueOf(-1), r.num());
        Assertions.assertEquals(BigInteger.valueOf(2), r.den());
    }

    @Test
    public void testAdd() {
        Rational r1 = Rational.from(1L, 3L);
        Rational r2 = Rational.from(1L, 6L);
        Assertions.assertEquals(Rational.from(1L, 2L), r1.add(r2));
    }

    @Test
    public void testSubtract() {
        Rational r1 = Rational.from(3L, 4L);
        Rational r2 = Rational.from(1L, 4L);
        Assertions.assertEquals(Rational.from(1L, 2L), r1.subtract(r2));
    }

    @Test
    public void testMultiply() {
        Rational r1 = Rational.from(2L, 3L);
        Rational r2 = Rational.from(3L, 4L);
        Assertions.assertEquals(Rational.from(1L, 2L), r1.multiply(r2));
    }

    @Test
    public void testDivide() {
        Rational r1 = Rational.from(1L, 2L);
        Rational r2 = Rational.from(1L, 4L);
        Assertions.assertEquals(Rational.from(2L, 1L), r1.divide(r2));
    }

    @Test
    public void testDivideByZeroThrows() {
        Rational r1 = Rational.from(1L, 2L);
        Rational r2 = Rational.ZERO;
        Assertions.assertThrows(IllegalArgumentException.class, () -> r1.divide(r2));
    }

    @Test
    public void testAbs() {
        Rational r = Rational.from(-2L, 3L);
        Assertions.assertEquals(Rational.from(2L, 3L), r.abs());
    }

    @Test
    public void testFloor() {
        Rational r = Rational.from(7L, 2L);
        Assertions.assertEquals(BigInteger.valueOf(3), r.floor());
    }

    @Test
    public void testCeiling() {
        Rational r = Rational.from(7L, 2L);
        Assertions.assertEquals(BigInteger.valueOf(4), r.ceiling());
    }

    @Test
    public void testToDouble() {
        Rational r = Rational.from(1L, 4L);
        Assertions.assertEquals(0.25, r.toDouble(), 0.0001);
    }

    @Test
    public void testEquals() {
        Rational r1 = Rational.from(2L, 4L);
        Rational r2 = Rational.from(1L, 2L);
        Assertions.assertEquals(r1, r2);
    }

    @Test
    public void testGenerateBigDecimal() {
        Rational r = Rational.generate(new BigDecimal("0.75"));
        Assertions.assertEquals(Rational.from(3L, 4L), r);
    }
}