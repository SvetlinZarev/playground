package com.github.svetlinzarev.playground.contest;

import com.github.svetlinzarev.playground.contest.euler.AllEulerTests;
import com.github.svetlinzarev.playground.contest.topcoder.AllTopCoderTests;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({AllEulerTests.class, AllTopCoderTests.class})
public class AllContestTests {
}
