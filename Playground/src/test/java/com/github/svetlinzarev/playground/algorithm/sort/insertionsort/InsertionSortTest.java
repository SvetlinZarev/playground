package com.github.svetlinzarev.playground.algorithm.sort.insertionsort;

import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.sort.common.SortTestBase;
import com.github.svetlinzarev.playground.algorithm.sort.insertionsort.InsertionSort;

public class InsertionSortTest extends SortTestBase {

    @Test
    public void testSort() throws Exception {
        testSort(InsertionSort::sort);
    }

    @Test
    public void testRepeatedlySortIntArray() {
        testRepeatedlySortIntArray(InsertionSort::sort);
    }

}
