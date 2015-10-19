package com.github.svetlinzarev.playground.algorithm.sort.insertionsort;

public class InsertionSort {
    private InsertionSort() {
        throw new AssertionError("This class is not meant to be instantiated");
    }

    public static int[] sort(int[] data) {
        for (int index = 1; index < data.length; index++) {
            for (int insertionPoint = 0; insertionPoint < index; insertionPoint++) {

                /*
                 * If the element at index is greater than the element at
                 * insertionPoint, remember element[index] , shift all elements
                 * from insertionPoint to index one position to the right and
                 * put the remembered element to element[insertionPoint]
                 */
                if (data[insertionPoint] > data[index]) {
                    int temp = data[index];

                    /*
                     * shift all elements from insertionPoint to index one
                     * position to the right
                     */

                    System.arraycopy(data, insertionPoint, data, insertionPoint + 1, index - insertionPoint);
                    data[insertionPoint] = temp;
                }
            }
        }

        return data;
    }
}
