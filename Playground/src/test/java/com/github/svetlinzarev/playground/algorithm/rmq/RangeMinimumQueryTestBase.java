package com.github.svetlinzarev.playground.algorithm.rmq;

import org.junit.Assert;

import com.github.svetlinzarev.playground.algorithm.rmq.RangeMinimumQuery;

public class RangeMinimumQueryTestBase {
    private static final int[] data = { 9, 5, 6, 1, 6, 2, 7, 3 };

    public int[] getData() {
        return data;
    }

    public void testQueryValue(RangeMinimumQuery rangeMinimumQuery) throws Exception {
        int actual = rangeMinimumQuery.queryValue(0, 2);
        Assert.assertEquals(5, actual);

        actual = rangeMinimumQuery.queryValue(2, 4);
        Assert.assertEquals(1, actual);

        actual = rangeMinimumQuery.queryValue(4, 6);
        Assert.assertEquals(2, actual);

        actual = rangeMinimumQuery.queryValue(0, 4);
        Assert.assertEquals(1, actual);

        actual = rangeMinimumQuery.queryValue(0, data.length - 1);
        Assert.assertEquals(1, actual);
    }

    public void testQueryIndex(RangeMinimumQuery rangeMinimumQuery) throws Exception {
        int actual = rangeMinimumQuery.queryIndex(0, 2);
        Assert.assertEquals(1, actual);

        actual = rangeMinimumQuery.queryIndex(2, 4);
        Assert.assertEquals(3, actual);

        actual = rangeMinimumQuery.queryIndex(4, 6);
        Assert.assertEquals(5, actual);

        actual = rangeMinimumQuery.queryIndex(0, 4);
        Assert.assertEquals(3, actual);

        actual = rangeMinimumQuery.queryIndex(0, data.length - 1);
        Assert.assertEquals(3, actual);
    }

}
