package com.github.svetlinzarev.playground.math.primes;

public final class Primes {
    private Primes() {
        throw new AssertionError("This class is not meant to be instantiated.");
    }

    public static boolean isPrime(final long number) {
        // Primes by definition are not negative. And 1 is not a prime too.
        if (1 >= number) {
            return false;
        }

        // If the number is 2 , then it's a prime
        if (2 == number) {
            return true;
        }

        // If the number is even, then it's not a prime
        if ((number & 1) == 0) {
            return false;
        }

        // check only with odd numbers
        final long upperLimit = (long) Math.sqrt(number);
        for (long x = 3; x <= upperLimit; x += 2) {
            if (number % x == 0) {
                return false;
            }
        }

        return true;
    }
}
