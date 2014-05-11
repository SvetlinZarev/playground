package com.github.svetlinzarev.playground.algorithm.sort.bubblesort;

public class BubbleSort {
    private BubbleSort() {
        throw new AssertionError("This class is not meant to be instantiated");
    }

    public static int[] sort(int[] data) {
        for (int i = 0; i < data.length; i++) {
            for (int j = i + 1; j < data.length; j++) {
                if (data[i] > data[j]) {
                    int temp = data[i];
                    data[i] = data[j];
                    data[j] = temp;
                }
            }
        }

        return data;
    }
}
