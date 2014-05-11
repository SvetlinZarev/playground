package com.github.svetlinzarev.playground.algorithm.sort.quizcksort;

public class QuickSort {
    private QuickSort() {
        throw new AssertionError("This class is not meant to be instantiated");
    }

    public static int[] sort(int[] data) {
        return sort(data, 0, data.length - 1);
    }

    static int[] sort(int[] data, int from, int to) {
        if (from < to) {
            int pivot = partition(data, from, to);
            sort(data, from, pivot - 1);
            sort(data, pivot + 1, to);
        }

        return data;
    }

    static int partition(int[] data, int from, int to) {
        int pivotValue = data[from];
        int left = from, right = to;

        while (left < right) {
            while (left < to && data[left] <= pivotValue) {
                left++;
            }

            while (data[right] > pivotValue) {
                right--;
            }

            if (left < right) {
                swap(data, left, right);
            }
        }
        data[from] = data[right];
        data[right] = pivotValue;
        return right;
    }

    static void swap(int[] data, int i, int j) {
        int temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }
}
