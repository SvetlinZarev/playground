package com.github.svetlinzarev.playground.algorithm.sort.bubblesort;

public class OptimizedBubbleSort {
    private OptimizedBubbleSort() {
        throw new AssertionError("This class is not meant to be instantiated");
    }

    public static int[] sort(int[] data) {
        int len = data.length;

        while (len != 0) {
            int newLen = 0;
            for (int i = 1; i < len; i++) {
                if (data[i - 1] > data[i]) {
                    int temp = data[i];
                    data[i] = data[i - 1];
                    data[i - 1] = temp;
                    newLen = i;
                }
            }
            len = newLen;
        }

        return data;
    }

}
