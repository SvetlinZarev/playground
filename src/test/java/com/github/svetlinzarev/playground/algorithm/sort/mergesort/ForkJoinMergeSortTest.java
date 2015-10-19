package com.github.svetlinzarev.playground.algorithm.sort.mergesort;

import java.util.concurrent.ForkJoinPool;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.sort.common.SortTestBase;
import com.github.svetlinzarev.playground.algorithm.sort.mergesort.ForkJoinMergeSort;

public class ForkJoinMergeSortTest extends SortTestBase {
    private ForkJoinPool forkJoinPool;

    @Before
    public void setUp() throws Exception {
        forkJoinPool = new ForkJoinPool();
    }

    @After
    public void tearDown() throws Exception {
        forkJoinPool.shutdown();
    }

    @Test
    public void testCompute() {
        testSort(data -> forkJoinPool.invoke(new ForkJoinMergeSort(data)));
    }

    @Test
    public void testRepeatCompute() {
        testRepeatedlySortIntArray(data -> forkJoinPool.invoke(new ForkJoinMergeSort(data)));
    }
}
