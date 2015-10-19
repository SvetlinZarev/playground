package com.github.svetlinzarev.playground.algorithm.rmq.trivial;

import org.junit.Before;
import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.rmq.RangeMinimumQueryTestBase;
import com.github.svetlinzarev.playground.algorithm.rmq.trivial.TrivialRMQApproach;

public class TrivialRMQApproachTest extends RangeMinimumQueryTestBase {
    private TrivialRMQApproach trivialRMQ;

    @Before
    public void setUp() {
        trivialRMQ = new TrivialRMQApproach(getData());
    }

    @Test
    public void testQueryValue() throws Exception {
        testQueryValue(trivialRMQ);
    }

    @Test
    public void testQueryIndex() throws Exception {
        testQueryIndex(trivialRMQ);
    }

}
