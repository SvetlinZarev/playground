package com.github.svetlinzarev.playground.math.whatever;

public final class MathUtil {
    private static final double LOG2 = Math.log(2.0);

    private MathUtil() {
        throw new AssertionError("This class is not meant to be instantiated");
    }

    public static boolean isPowerOfTwo(long number) {
        return 0 < number & (number & (number - 1)) == 0;
    }

    public static double log2(double x) {
        return Math.log(x) / LOG2;
    }

}
