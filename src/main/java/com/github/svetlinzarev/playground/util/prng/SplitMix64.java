package com.github.svetlinzarev.playground.util.prng;

// http://xoroshiro.di.unimi.it/splitmix64.c
public final class SplitMix64 extends AbstractRandomNumberGenerator {
    private long state = initialSeed();

    @Override
    public long nextValue() {
        state += 0x9e3779b97f4a7c15L;
        long z = state;
        z = (z ^ (z >>> 30)) * 0xbf58476d1ce4e5b9L;
        z = (z ^ (z >>> 27)) * 0x94d049bb133111ebL;
        return z ^ (z >>> 31);
    }
}
