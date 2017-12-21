package com.github.svetlinzarev.playground.util.prng;


abstract class AbstractRandomNumberGenerator implements RandomNumberGenerator {

    protected long initialSeed() {
        return (long) (Long.MAX_VALUE * Math.random());
    }

    // algorithm from ThreadLocalRandom.internalNextLong()
    @Override
    public long nextValue(long min, long max) {
        if (min >= max | min < 0) {
            throw new IllegalArgumentException("Invalid range: Min=" + min + "\tMax=" + max);
        }

        final long n = max - min;
        final long m = n - 1;

        long result = nextValue();

        if ((n & m) == 0L) {  // power of two
            result = (result & m) + min;
        } else {
            // ensure non-negative: >>> 1
            for (long u = result >>> 1; u + m - (result = u % n) < 0L; u = nextValue() >>> 1) {
                //no-op
            }

            result += min;
        }
        return result;
    }
}
