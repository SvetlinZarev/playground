package com.github.svetlinzarev.playground.algorithm.rmq;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.github.svetlinzarev.playground.algorithm.rmq.segmenttree.IntRMQSegmentTreeTest;
import com.github.svetlinzarev.playground.algorithm.rmq.sparsetable.SparseTableRMQApproachTest;
import com.github.svetlinzarev.playground.algorithm.rmq.sqrtarray.SqrtArrayRMQApproachTest;
import com.github.svetlinzarev.playground.algorithm.rmq.trivial.TrivialRMQApproachTest;

@RunWith(Suite.class)
@SuiteClasses({ IntRMQSegmentTreeTest.class, SparseTableRMQApproachTest.class, SqrtArrayRMQApproachTest.class,
        TrivialRMQApproachTest.class })
public class AllRMQTests {

}
