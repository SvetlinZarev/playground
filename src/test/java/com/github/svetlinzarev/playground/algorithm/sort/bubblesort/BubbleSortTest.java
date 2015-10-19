package com.github.svetlinzarev.playground.algorithm.sort.bubblesort;

import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.sort.bubblesort.BubbleSort;
import com.github.svetlinzarev.playground.algorithm.sort.common.SortTestBase;

public class BubbleSortTest extends SortTestBase {

    @Test
    public void testSort() throws Exception {
        testSort(BubbleSort::sort);
    }

    @Test
    public void testRepeatedlySortIntArray() {
        testRepeatedlySortIntArray(BubbleSort::sort);
    }
}
