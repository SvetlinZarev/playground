package com.github.svetlinzarev.playground.util.prng;

class XoRoShiRo128PlusTest extends RandomNumberGeneratorTestBase {
    @Override
    protected RandomNumberGenerator createRandomNumberGenerator() {
        return new XoRoShiRo128Plus();
    }
}
