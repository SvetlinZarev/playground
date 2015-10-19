package com.github.svetlinzarev.playground.algorithm.sort.mergesort;

class MergeSortUtil {
    private MergeSortUtil() {
        throw new AssertionError("This class is not meant to be instantiated");
    }

    public static final int[] merge(int[] data, int[] helper, int from, int midddle, int to) {
        System.arraycopy(data, from, helper, from, to - from + 1);

        int index = from;
        int low = from;
        int mid = midddle + 1;

        while (low <= midddle && mid <= to) {
            if (helper[low] <= helper[mid]) {
                data[index] = helper[low++];
            } else {
                data[index] = helper[mid++];
            }
            ++index;
        }

        while (low <= midddle) {
            data[index++] = helper[low++];
        }

        return data;
    }
}
