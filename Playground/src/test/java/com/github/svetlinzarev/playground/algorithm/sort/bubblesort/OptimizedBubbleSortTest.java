package com.github.svetlinzarev.playground.algorithm.sort.bubblesort;

import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.sort.bubblesort.OptimizedBubbleSort;
import com.github.svetlinzarev.playground.algorithm.sort.common.SortTestBase;

public class OptimizedBubbleSortTest extends SortTestBase {

    @Test
    public void testSort() throws Exception {
        testSort(OptimizedBubbleSort::sort);
    }

    @Test
    public void testRepeatedlySortIntArray() {
        testRepeatedlySortIntArray(OptimizedBubbleSort::sort);
    }

}
