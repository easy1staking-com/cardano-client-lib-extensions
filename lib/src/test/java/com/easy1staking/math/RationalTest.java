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

    // Additional floor/ceiling tests for qToken conversion scenarios

    @Test
    public void testFloorExactDivision() {
        // When division is exact, floor and ceiling should be equal
        Rational r = Rational.from(10L, 2L); // 10/2 = 5
        Assertions.assertEquals(BigInteger.valueOf(5), r.floor());
    }

    @Test
    public void testCeilingExactDivision() {
        // When division is exact, floor and ceiling should be equal
        Rational r = Rational.from(10L, 2L); // 10/2 = 5
        Assertions.assertEquals(BigInteger.valueOf(5), r.ceiling());
    }

    @Test
    public void testFloorRoundsDown() {
        // 10/3 = 3.333... should floor to 3
        Rational r = Rational.from(10L, 3L);
        Assertions.assertEquals(BigInteger.valueOf(3), r.floor());
    }

    @Test
    public void testCeilingRoundsUp() {
        // 10/3 = 3.333... should ceiling to 4
        Rational r = Rational.from(10L, 3L);
        Assertions.assertEquals(BigInteger.valueOf(4), r.ceiling());
    }

    @Test
    public void testFloorNegativeNumber() {
        // -7/2 = -3.5 should floor to -4 (more negative)
        Rational r = Rational.from(-7L, 2L);
        Assertions.assertEquals(BigInteger.valueOf(-4), r.floor());
    }

    @Test
    public void testCeilingNegativeNumber() {
        // -7/2 = -3.5 should ceiling to -3 (less negative)
        Rational r = Rational.from(-7L, 2L);
        Assertions.assertEquals(BigInteger.valueOf(-3), r.ceiling());
    }

    @Test
    public void testFloorLargeNumbers() {
        // Simulating qToken rate scenario: 1000000 / 1012345 (rate slightly > 1)
        BigInteger amount = BigInteger.valueOf(1_000_000_000_000L); // 1 trillion lovelace
        BigInteger rateNum = BigInteger.valueOf(1_012_345L);
        BigInteger rateDen = BigInteger.valueOf(1_000_000L);

        // amount / rate = amount * rateDen / rateNum
        Rational r = Rational.from(amount.multiply(rateDen), rateNum);
        BigInteger floored = r.floor();
        BigInteger ceiled = r.ceiling();

        // Ceiling should be >= floor
        Assertions.assertTrue(ceiled.compareTo(floored) >= 0);
        // They should differ by at most 1
        Assertions.assertTrue(ceiled.subtract(floored).compareTo(BigInteger.ONE) <= 0);
    }

    @Test
    public void testQTokenConversionRoundTrip() {
        // Simulate: underlying -> qToken -> underlying
        // qTokenRate = 1.0123 (1 qToken = 1.0123 underlying)
        Rational qTokenRate = Rational.from(10123L, 10000L);
        BigInteger underlyingAmount = BigInteger.valueOf(1_000_000L); // 1 ADA

        // underlying -> qToken: should use CEILING
        // qTokens = ceil(underlying / rate)
        BigInteger qTokens = Rational.from(underlyingAmount).divide(qTokenRate).ceiling();

        // qToken -> underlying: should use FLOOR
        // actualUnderlying = floor(qTokens * rate)
        BigInteger actualUnderlying = Rational.from(qTokens).multiply(qTokenRate).floor();

        // actualUnderlying should be >= original (we burned enough qTokens)
        Assertions.assertTrue(actualUnderlying.compareTo(underlyingAmount) >= 0,
            "actualUnderlying (" + actualUnderlying + ") should be >= underlyingAmount (" + underlyingAmount + ")");
    }

    @Test
    public void testQTokenConversionFloorCausesProblem() {
        // Demonstrate why using floor for underlying -> qToken is wrong
        Rational qTokenRate = Rational.from(10123L, 10000L);
        BigInteger underlyingAmount = BigInteger.valueOf(1_000_000L);

        // WRONG: using floor for underlying -> qToken
        BigInteger qTokensWrong = Rational.from(underlyingAmount).divide(qTokenRate).floor();
        BigInteger actualUnderlyingWrong = Rational.from(qTokensWrong).multiply(qTokenRate).floor();

        // CORRECT: using ceiling for underlying -> qToken
        BigInteger qTokensCorrect = Rational.from(underlyingAmount).divide(qTokenRate).ceiling();
        BigInteger actualUnderlyingCorrect = Rational.from(qTokensCorrect).multiply(qTokenRate).floor();

        // With floor, you might get less than requested
        // With ceiling, you always get at least what you requested
        Assertions.assertTrue(actualUnderlyingCorrect.compareTo(underlyingAmount) >= 0,
            "Ceiling approach should give >= requested amount");

        // The difference shows the bug - floor may give less
        System.out.println("Floor approach: requested=" + underlyingAmount + ", got=" + actualUnderlyingWrong);
        System.out.println("Ceiling approach: requested=" + underlyingAmount + ", got=" + actualUnderlyingCorrect);
    }
}