package com.github.svetlinzarev.playground.math.primes;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.svetlinzarev.playground.math.primes.Primes;

public class PrimesTest {

    @Test
    public void testIsPrime() {
        assertEquals(false, Primes.isPrime(1));
        assertEquals(true, Primes.isPrime(2));
        assertEquals(false, Primes.isPrime(4));

        for (long x : PrimeList.notPrimes) {
            assertEquals("" + x, false, Primes.isPrime(x));
        }

        for (long x : PrimeList.firstThousandPrimes) {
            assertEquals("" + x, true, Primes.isPrime(x));
        }

        for (long x : PrimeList.randomBigPrimes) {
            assertEquals("" + x, true, Primes.isPrime(x));
        }
    }
}
