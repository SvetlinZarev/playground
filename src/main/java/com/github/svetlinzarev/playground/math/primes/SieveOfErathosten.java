package com.github.svetlinzarev.playground.math.primes;

import java.util.LinkedList;
import java.util.List;

public final class SieveOfErathosten {
    private SieveOfErathosten() {
        throw new AssertionError("This class is not meant to be instantiated");
    }

    public static List<Integer> getPrimes(final int upperLimit) {
        if (1 >= upperLimit || upperLimit == Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Invalid upper limit: " + upperLimit);
        }

        final List<Integer> primes = new LinkedList<>();
        final boolean isComposite[] = new boolean[upperLimit + 1];
        isComposite[0] = isComposite[1] = true; // 0 and 1 are not primes

        final int upperLimitSqrt = (int) Math.sqrt(upperLimit);
        for (int x = 2; x <= upperLimitSqrt; x++) {
            if (!isComposite[x]) {
                for (int z = x * x; z <= upperLimit; z += x) {
                    isComposite[z] = true;
                }
            }
        }

        primes.add(2);
        // Skip even numbers, for they are not primes
        for (int i = 3; i < isComposite.length; i += 2) {
            if (!isComposite[i]) {
                primes.add(i);
            }
        }
        return primes;
    }
}
