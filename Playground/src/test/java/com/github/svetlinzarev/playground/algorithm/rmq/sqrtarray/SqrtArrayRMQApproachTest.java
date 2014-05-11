package com.github.svetlinzarev.playground.algorithm.rmq.sqrtarray;

import org.junit.Before;
import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.rmq.RangeMinimumQueryTestBase;
import com.github.svetlinzarev.playground.algorithm.rmq.sqrtarray.SqrtArrayRMQApproach;

public class SqrtArrayRMQApproachTest extends RangeMinimumQueryTestBase {
    private SqrtArrayRMQApproach sqrtArrayRMQ;

    @Before
    public void setUp() {
        sqrtArrayRMQ = new SqrtArrayRMQApproach(getData());
    }

    @Test
    public void testQueryValue() throws Exception {
        testQueryValue(sqrtArrayRMQ);
    }

    @Test
    public void testQueryIndex() throws Exception {
        testQueryIndex(sqrtArrayRMQ);
    }
}
