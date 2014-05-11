package com.github.svetlinzarev.playground.algorithm.rmq.sparsetable;

import org.junit.Before;
import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.rmq.RangeMinimumQueryTestBase;
import com.github.svetlinzarev.playground.algorithm.rmq.sparsetable.SparseTableRMQApproach;

public class SparseTableRMQApproachTest extends RangeMinimumQueryTestBase {
    private SparseTableRMQApproach sparseTableRMQApproach;

    @Before
    public void setUp() {
        sparseTableRMQApproach = new SparseTableRMQApproach(getData());
    }

    @Test
    public void testQueryValue() throws Exception {
        testQueryValue(sparseTableRMQApproach);
    }

    @Test
    public void testQueryIndex() throws Exception {
        testQueryIndex(sparseTableRMQApproach);
    }
}
