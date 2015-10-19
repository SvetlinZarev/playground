package com.github.svetlinzarev.playground.algorithm.sort.mergesort;

import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.sort.common.SortTestBase;
import com.github.svetlinzarev.playground.algorithm.sort.mergesort.RecursiveMergeSort;

public class RecursiveMergeSortTest extends SortTestBase {

    @Test
    public void testSort() {
        testSort(RecursiveMergeSort::sort);
    }

    @Test
    public void testRepeatedlySortIntArray() {
        testRepeatedlySortIntArray(RecursiveMergeSort::sort);
    }
}
