package com.github.svetlinzarev.playground;

import com.github.svetlinzarev.playground.algorithm.AllAlgorithmTests;
import com.github.svetlinzarev.playground.contest.AllContestTests;
import com.github.svetlinzarev.playground.math.AllMathTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({AllContestTests.class, AllAlgorithmTests.class, AllMathTests.class})
public class AllTests {

}
