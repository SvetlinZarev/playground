package com.github.svetlinzarev.playground.math.whatever;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.svetlinzarev.playground.math.whatever.MathUtil;

public class MathUtilTest {
    private static final int LIMIT = 1024 * 128;

    @Test
    public void testIsPowerOfTwoWIthNegativeNumbers() {
        for (int i = -1; i > -LIMIT; i--) {
            assertFalse("Number=" + i, MathUtil.isPowerOfTwo(i));
        }
    }

    @Test
    public void testIsPowerOfTwoWIthPowerOfTwoNumbers() {
        for (int i = 1; i <= LIMIT; i *= 2) {
            assertTrue("Number=" + i, MathUtil.isPowerOfTwo(i));
        }
    }

    @Test
    public void testIsPowerOfTwoWithZero() {
        assertFalse(MathUtil.isPowerOfTwo(0));
    }

    @Test
    public void testIsPowerOfTwoWIthPositiveNumbers() {
        assertFalse(MathUtil.isPowerOfTwo(3));
        assertFalse(MathUtil.isPowerOfTwo(5));
        assertFalse(MathUtil.isPowerOfTwo(6));
        assertFalse(MathUtil.isPowerOfTwo(7));
        assertFalse(MathUtil.isPowerOfTwo(10));
        assertFalse(MathUtil.isPowerOfTwo(260));
        assertFalse(MathUtil.isPowerOfTwo(2000));
    }
}
