package com.github.svetlinzarev.playground.math;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.svetlinzarev.playground.math.primes.AllPrimesTests;
import com.github.svetlinzarev.playground.math.whatever.AllWhateverTests;

@RunWith(Suite.class)
@SuiteClasses({ AllPrimesTests.class, AllWhateverTests.class })
public class AllMathTests {

}
