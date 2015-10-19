package com.github.svetlinzarev.playground;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.svetlinzarev.playground.algorithm.AllAlgorithmTests;
import com.github.svetlinzarev.playground.euler.AllEulerTests;
import com.github.svetlinzarev.playground.math.AllMathTests;

@RunWith(Suite.class)
@SuiteClasses({ AllEulerTests.class, AllAlgorithmTests.class, AllMathTests.class })
public class AllTests {

}
