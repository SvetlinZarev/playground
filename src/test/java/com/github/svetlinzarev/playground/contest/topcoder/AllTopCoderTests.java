package com.github.svetlinzarev.playground.contest.topcoder;

import com.github.svetlinzarev.playground.contest.topcoder.badneighbors.IterativeBadNeighborsTest;
import com.github.svetlinzarev.playground.contest.topcoder.badneighbors.RecursiveBadNeighborsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({IterativeBadNeighborsTest.class, RecursiveBadNeighborsTest.class})
public class AllTopCoderTests {
}
