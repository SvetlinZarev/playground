package com.github.svetlinzarev.playground.algorithm.sort.common;

import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.Assert;

public class SortTestBase {
    private final Random random = new Random();
    private static final int DEFAULT_REPETITIONS = 512;
    private static final int DEFAULT_DATA_SIZE = 1567;
    private int[] shuffled;
    private int[] sorted;

    private void prepareTestData(int dataSize) {
        shuffled = new int[dataSize];

        for (int i = 0; i < dataSize; i++) {
            shuffled[i] = random.nextInt(dataSize);
        }

        sorted = Arrays.copyOf(shuffled, shuffled.length);
        Arrays.sort(sorted);
    }

    protected void testSort(SortingAlgorithm sortingAlgorithm) {
        prepareTestData(DEFAULT_DATA_SIZE);

        final int[] afterSorting = sortingAlgorithm.sort(shuffled);
        boolean equal = Arrays.equals(sorted, afterSorting);

        if (!equal) {
            Assert.fail("Arrays are not equal after sorting");
        }
    }

    protected void testRepeatedlySortIntArray(SortingAlgorithm sortingAlgorithm) {
        for (int r = 0; r <= DEFAULT_REPETITIONS; r++) {
            prepareTestData(r);
            int[] afterSorting = sortingAlgorithm.sort(shuffled);
            assertArrayEquals("Data size = " + r, sorted, afterSorting);
        }
    }
}
