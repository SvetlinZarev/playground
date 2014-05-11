package com.github.svetlinzarev.playground.algorithm.arrays;

import org.junit.Assert;
import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.arrays.LongestIncreasingSubsequence;

public class LongestIncreasingSubsequenceTest {

    @Test
    public void testDynamicProgrammingApproach() throws Exception {
        int[] input = { 9, 6, 2, 7, 4, 7, 6, 5, 8, 4 };
        int expectedLength = 4;
        int actualLength = LongestIncreasingSubsequence.dynamicProgrammingApproach(input);
        Assert.assertEquals(expectedLength, actualLength);

        input = new int[] { 0, 2, 9, 5, 3, 9, 7, 4, 9, 6 };
        expectedLength = 5;
        actualLength = LongestIncreasingSubsequence.dynamicProgrammingApproach(input);
        Assert.assertEquals(expectedLength, actualLength);
    }
}
