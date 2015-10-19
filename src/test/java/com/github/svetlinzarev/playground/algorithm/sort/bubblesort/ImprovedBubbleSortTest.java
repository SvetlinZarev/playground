package com.github.svetlinzarev.playground.algorithm.sort.bubblesort;

import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.sort.bubblesort.ImprovedBubbleSort;
import com.github.svetlinzarev.playground.algorithm.sort.common.SortTestBase;

public class ImprovedBubbleSortTest extends SortTestBase {

    @Test
    public void testSort() throws Exception {
        testSort(ImprovedBubbleSort::sort);
    }

    @Test
    public void testRepeatedlySortIntArray() {
        testRepeatedlySortIntArray(ImprovedBubbleSort::sort);
    }
}
