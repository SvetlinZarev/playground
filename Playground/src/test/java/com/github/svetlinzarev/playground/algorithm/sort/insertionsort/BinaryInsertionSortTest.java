package com.github.svetlinzarev.playground.algorithm.sort.insertionsort;

import org.junit.Assert;
import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.sort.common.SortTestBase;
import com.github.svetlinzarev.playground.algorithm.sort.insertionsort.BinaryInsertionSort;

public class BinaryInsertionSortTest extends SortTestBase {

    @Test
    public void testSort() throws Exception {
        testSort(BinaryInsertionSort::sort);
    }

    @Test
    public void testRepeatedlySortIntArray() {
        testRepeatedlySortIntArray(BinaryInsertionSort::sort);
    }

    @Test
    public void testBinarySearch() {
        int position;

        /* Test with data.length % 2 == 0 */
        int[] data = { 0, 1, 2, 3, 4, 5 };
        for (int i = 0; i < data.length; i++) {
            position = BinaryInsertionSort.binarySearch(data, i, 0, data.length - 1);
            Assert.assertEquals(i, position);
        }
        testSpecialCases(data);

        /* Test with data.length % 2 != 0 */
        data = new int[] { 0, 1, 2, 3, 4, 5, 6 };
        for (int i = 0; i < data.length; i++) {
            position = BinaryInsertionSort.binarySearch(data, i, 0, data.length - 1);
            Assert.assertEquals(i, position);
        }
        testSpecialCases(data);
    }

    @Test
    public void testRecursiveBinarySearch() {
        /* Test with data.length % 2 == 0 */
        int[] data = { 0, 1, 2, 3, 4, 5 };
        for (int i = 0; i < data.length; i++) {
            int position = BinaryInsertionSort.recursiveBinarySearch(data, i, 0, data.length - 1);
            Assert.assertEquals(i, position);
        }
        testSpecialCases(data);

        /* Test with data.length % 2 != 0 */
        data = new int[] { 0, 1, 2, 3, 4, 5, 6 };
        for (int i = 0; i < data.length; i++) {
            int position = BinaryInsertionSort.recursiveBinarySearch(data, i, 0, data.length - 1);
            Assert.assertEquals(i, position);
        }
        testSpecialCases(data);
    }

    private void testSpecialCases(int[] data) {
        int position;
        /*
         * test the special case, when the key is not in the array - key greater
         * than all elements
         */
        position = BinaryInsertionSort.binarySearch(data, data.length, 0, data.length - 1);
        Assert.assertEquals(data.length - 1, position);

        /*
         * test the special case, when the key is not in the array - key lesser
         * than all elements
         */
        position = BinaryInsertionSort.binarySearch(data, -1, 0, data.length - 1);
        Assert.assertEquals(0, position);
    }

}
