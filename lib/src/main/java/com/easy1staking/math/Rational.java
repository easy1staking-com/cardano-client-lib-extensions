package com.easy1staking.math;


import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

@Slf4j
public record Rational(BigInteger num, BigInteger den) {

    public Rational {
        if (BigInteger.ZERO.equals(den)) {
            throw new IllegalArgumentException("Denominator cannot be zero");
        }
        // Normalize sign
        if (den.signum() < 0) {
            num = num.negate();
            den = den.negate();
        }
        // Reduce to lowest terms
        BigInteger gcd = num.gcd(den);
        num = num.divide(gcd);
        den = den.divide(gcd);
    }

    public static final Rational ZERO = Rational.from(0L);

    public static final Rational ONE = Rational.from(1L);

    public static Rational from(Long num, Long den) {
        return new Rational(BigInteger.valueOf(num), BigInteger.valueOf(den));
    }

    public static Rational from(BigInteger num, BigInteger den) {
        return new Rational(num, den);
    }

    public static Rational from(Long value) {
        return new Rational(BigInteger.valueOf(value), BigInteger.ONE);
    }

    public static Rational from(BigInteger value) {
        return new Rational(value, BigInteger.ONE);
    }

    public static Rational generate(BigDecimal decimalNumber) {
        int scale = decimalNumber.scale();
        BigInteger num = decimalNumber.unscaledValue();
        BigInteger den = BigInteger.TEN.pow(scale);
        BigInteger gcd = num.gcd(den);
        return Rational.from(num.divide(gcd), den.divide(gcd));
    }
    public static Rational generate(Double decimalNumber) {
        return generate(BigDecimal.valueOf(decimalNumber));
    }

    public Rational add(Rational that) {
        return new Rational(this.num.multiply(that.den).add(this.den.multiply(that.num)), this.den.multiply(that.den));
    }

    public Rational subtract(Rational that) {
        return this.add(that.negate());
    }

    public Rational multiply(Rational that) {
        return new Rational(this.num.multiply(that.num), this.den.multiply(that.den));
    }

    public Rational divide(Rational that) {
        return new Rational(this.num.multiply(that.den), this.den.multiply(that.num));
    }

    public Rational negate() {
        return new Rational(this.num.negate(), this.den);
    }

    public Rational max(Rational that) {
        if (this.gt(that)) {
            return this;
        } else {
            return that;
        }
    }

    public boolean lt(Rational that) {
        return (this.num.multiply(that.den)).compareTo((that.num.multiply(this.den))) < 0;
    }

    public boolean gt(Rational that) {
        return (this.num.multiply(that.den)).compareTo((that.num.multiply(this.den))) > 0;
    }

    public BigInteger floor() {
        return new BigDecimal(num).divide(new BigDecimal(den), RoundingMode.FLOOR).toBigInteger();
    }

    public BigInteger ceiling() {
        return new BigDecimal(num).divide(new BigDecimal(den), RoundingMode.CEILING).toBigInteger();
    }

    public Double toDouble() {
        return (new BigDecimal(num)).divide(new BigDecimal(den), 4, RoundingMode.FLOOR).doubleValue();
    }

    public Rational abs() {
        return Rational.from(num.abs(), den.abs());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Rational(BigInteger thatNum, BigInteger thatDen)) {
            return this.num.multiply(thatDen).equals(this.den.multiply(thatNum));
        }
        return false;
    }
}
