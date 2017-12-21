package com.github.svetlinzarev.playground.util.prng;


// http://xoroshiro.di.unimi.it/xoroshiro128plus.c
public final class XoRoShiRo128Plus extends AbstractRandomNumberGenerator {
    private long s0, s1;

    public XoRoShiRo128Plus() {
        s0 = initialSeed();
        s1 = initialSeed();
    }

    @Override
    public long nextValue() {
        final long x = s0 ^ s1;
        s0 = Long.rotateLeft(s0, 55) ^ x ^ (x << 14);
        s1 = Long.rotateLeft(x, 36);
        return s0 + s1;
    }
}
