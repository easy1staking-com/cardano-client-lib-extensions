package com.easy1staking.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class HashUtil {

    private static final Random RANDOM = new Random();

    public static String getRandomHash() {
        var longNumber = RANDOM.nextLong();
        var hex = Long.toHexString(longNumber);
        return String.format("%s%s%s%s", hex, hex, hex, hex);
    }

}
