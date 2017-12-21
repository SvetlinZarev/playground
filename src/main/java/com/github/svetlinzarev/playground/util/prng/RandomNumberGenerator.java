package com.github.svetlinzarev.playground.util.prng;


public interface RandomNumberGenerator {
    long nextValue();

    long nextValue(long min, long max);
}
