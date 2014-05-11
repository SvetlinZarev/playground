package com.github.svetlinzarev.playground.algorithm.sort.selectionsort;

public class SelectionSort {
    private SelectionSort() {
        throw new AssertionError("This class is not meant to be instantiated");
    }

    public static int[] sort(int[] data) {
        for (int i = 0; i < data.length; i++) {
            int minIndex = i;

            for (int j = i + 1; j < data.length; j++) {
                if (data[j] < data[minIndex]) {
                    minIndex = j;
                }
            }

            int temp = data[i];
            data[i] = data[minIndex];
            data[minIndex] = temp;
        }

        return data;
    }
}
