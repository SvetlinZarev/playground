package com.github.svetlinzarev.playground.util.prng;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class RandomNumberGeneratorTestBase {
    private static final int FEW_ITERATIONS = 100;
    private static final int MANY_ITERATIONS = 1_000_000;

    private RandomNumberGenerator generator;

    @BeforeEach
    public void beforeEach() {
        generator = createRandomNumberGenerator();
    }

    protected abstract RandomNumberGenerator createRandomNumberGenerator();

    @Test
    public void testNextInRange() {
        testInRange(FEW_ITERATIONS, 21, 22);
        testInRange(MANY_ITERATIONS, 11, 997);
        testInRange(MANY_ITERATIONS, 1033, 61043);
    }

    @Test
    public void testInPowerOfTwoRange() {
        testInRange(FEW_ITERATIONS, 0, 1); // 1
        testInRange(FEW_ITERATIONS, 0, 2); // 2
        testInRange(MANY_ITERATIONS, 512, 1024); // 512
        testInRange(MANY_ITERATIONS, 1024 * 7, 1024 * (512 + 7)); // 1024 * 512
    }

    private void testInRange(int iterations, long minIncl, long maxExcl) {
        for (int i = 0; i < iterations; i++) {
            final long value = generator.nextValue(minIncl, maxExcl);
            assertTrue(value >= 0, "Value is negative: " + value);
            assertTrue(value >= minIncl, "Value is less than the specified min(" + minIncl + "): " + value);
            assertTrue(value < maxExcl, "Value is greater or equal to the specified max(" + maxExcl + "): " + value);
        }
    }

}
