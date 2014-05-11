package com.github.svetlinzarev.playground.algorithm.sort.quizcksort;

import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.sort.common.SortTestBase;
import com.github.svetlinzarev.playground.algorithm.sort.quizcksort.QuickSort;

public class QuickSortTest extends SortTestBase {

    @Test(timeout = 1000)
    public void testSort() throws Exception {
        testSort(QuickSort::sort);
    }

    @Test(timeout = 1000)
    public void testRepeatedlySortIntArray() {
        testRepeatedlySortIntArray(QuickSort::sort);
    }
}
