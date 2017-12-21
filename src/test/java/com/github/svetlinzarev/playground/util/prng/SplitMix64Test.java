package com.github.svetlinzarev.playground.util.prng;

class SplitMix64Test extends RandomNumberGeneratorTestBase {
    @Override
    protected RandomNumberGenerator createRandomNumberGenerator() {
        return new SplitMix64();
    }
}
