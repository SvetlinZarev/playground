package com.github.svetlinzarev.playground.algorithm.sort.mergesort;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class MergeSortUtilTest {

    @Test
    public void testMerge() {
        int[] helper = new int[5];

        int[] data = MergeSortUtil.merge(new int[] { 0, 1 }, helper, 0, 0, 1);
        assertArrayEquals(new int[] { 0, 1 }, data);

        data = MergeSortUtil.merge(new int[] { 1, 0 }, helper, 0, 0, 1);
        assertArrayEquals(new int[] { 0, 1 }, data);

        data = MergeSortUtil.merge(new int[] { 0, 1, 2, 3 }, helper, 0, 1, 3);
        assertArrayEquals(new int[] { 0, 1, 2, 3 }, data);

        data = MergeSortUtil.merge(new int[] { 2, 3, 0, 1 }, helper, 0, 1, 3);
        assertArrayEquals(new int[] { 0, 1, 2, 3 }, data);

        data = MergeSortUtil.merge(new int[] { 1, 3, 0, 2 }, helper, 0, 1, 3);
        assertArrayEquals(new int[] { 0, 1, 2, 3 }, data);

        data = MergeSortUtil.merge(new int[] { 0, 3, 1, 2 }, helper, 0, 1, 3);
        assertArrayEquals(new int[] { 0, 1, 2, 3 }, data);

        data = MergeSortUtil.merge(new int[] { 0, 1, 2, 3, 4 }, helper, 0, 2, 4);
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, data);

        data = MergeSortUtil.merge(new int[] { 1, 2, 3, 0, 4 }, helper, 0, 2, 4);
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, data);

        data = MergeSortUtil.merge(new int[] { 1, 2, 4, 0, 3 }, helper, 0, 2, 4);
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, data);

        data = MergeSortUtil.merge(new int[] { 1, 3, 4, 0, 2 }, helper, 0, 2, 4);
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, data);

        data = MergeSortUtil.merge(new int[] { 2, 3, 4, 0, 1 }, helper, 0, 2, 4);
        assertArrayEquals(new int[] { 0, 1, 2, 3, 4 }, data);
    }
}
