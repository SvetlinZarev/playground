package com.github.svetlinzarev.playground.math.primes;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.github.svetlinzarev.playground.math.primes.SieveOfErathosten;

public class SieveOfErathostenTest {

    @Test
    public void testGetPrimes() {
        List<Integer> shouldBePrimes = SieveOfErathosten.getPrimes(1000);

        int index = 0;
        for (Integer fromSieve : shouldBePrimes) {
            Integer fromList = PrimeList.firstThousandPrimes[index++];
            assertEquals("From list: " + fromList + "\tFrom sieve: " + fromSieve, fromList, fromSieve);

            if (index > PrimeList.firstThousandPrimes.length) {
                break;
            }
        }
    }
}
