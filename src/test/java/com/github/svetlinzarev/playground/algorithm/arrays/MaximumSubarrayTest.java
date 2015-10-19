package com.github.svetlinzarev.playground.algorithm.arrays;

import org.junit.Assert;
import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.arrays.MaximumSubarray;
import com.github.svetlinzarev.playground.algorithm.arrays.MaximumSubarray.Subarray;

public class MaximumSubarrayTest {

    @Test
    public void testMax() throws Exception {
        int[] array = { 1, 2, 3, 4, 5 };
        Subarray subarray = MaximumSubarray.max(array);
        Assert.assertEquals(new Subarray(array, 0, 4, 15), subarray);

        array = new int[] { 5, -4, -3, 7, 3 };
        subarray = MaximumSubarray.max(array);
        Assert.assertEquals(new Subarray(array, 3, 4, 10), subarray);

        array = new int[] { -5, -4, -3, -7, -3 };
        subarray = MaximumSubarray.max(array);
        Assert.assertEquals(new Subarray(array, 4, 4, -3), subarray);
    }
}
