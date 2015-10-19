package com.github.svetlinzarev.playground.algorithm.sort.bubblesort;

public class ImprovedBubbleSort {
    private ImprovedBubbleSort() {
        throw new AssertionError("This class is not meant to be instantiated");
    }

    public static int[] sort(int[] data) {
        boolean swapped = false;

        do {
            swapped = false;
            for (int i = 1; i < data.length; i++) {
                if (data[i - 1] > data[i]) {
                    int temp = data[i];
                    data[i] = data[i - 1];
                    data[i - 1] = temp;
                    swapped = true;
                }
            }
        } while (swapped);
        return data;
    }
}
