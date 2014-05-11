package com.github.svetlinzarev.playground.algorithm.sort.selectionsort;

import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.sort.common.SortTestBase;
import com.github.svetlinzarev.playground.algorithm.sort.selectionsort.SelectionSort;

public class SelectionSortTest extends SortTestBase {

    @Test
    public void testSort() throws Exception {
        testSort(SelectionSort::sort);
    }

    @Test
    public void testRepeatedlySortIntArray() {
        testRepeatedlySortIntArray(SelectionSort::sort);
    }

}
