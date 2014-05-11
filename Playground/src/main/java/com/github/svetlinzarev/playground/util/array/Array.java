package com.github.svetlinzarev.playground.util.array;

import java.io.Serializable;
import java.util.Random;

public final class Array implements Serializable {
    private static final long serialVersionUID = 1L;

    private Array() {
        // prevent instantiation
    }

    public static int[] random(int len) {
        return random(len, 0, Integer.MAX_VALUE);
    }

    public static int[] random(int len, int min, int max) {
        requirePositiveLength(len);
        requireValidLowerBoundary(min);
        requireValidUpperBoundary(min, max);

        int[] array = new int[len];
        Random random = new Random();

        for (int i = 0; i < array.length; i++) {
            array[i] = min + random.nextInt(max - min);
        }

        return array;
    }

    private static void requirePositiveLength(int len) {
        if (0 >= len) {
            throw new IllegalArgumentException("The array length must be positive. Length=" + len);
        }
    }

    private static void requireValidLowerBoundary(int min) {
        if (0 > min) {
            throw new IllegalArgumentException(String.format("The min  boundary must be positive. Min=%d", min));
        }
    }

    private static void requireValidUpperBoundary(int min, int max) {
        if (max < min) {
            String message = String.format("The upper bound is less than the lower bound. Min=%d\tMax=%d", min, max);
            throw new IllegalArgumentException(message);
        }

        if (0 == max && 0 == min) {
            throw new IllegalArgumentException("Bot max and min boundaries cannot be zero.");
        }
    }
}
