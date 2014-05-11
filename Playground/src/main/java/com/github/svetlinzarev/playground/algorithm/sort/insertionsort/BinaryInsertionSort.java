package com.github.svetlinzarev.playground.algorithm.sort.insertionsort;

public class BinaryInsertionSort {
	private BinaryInsertionSort() {
		throw new AssertionError("This class is not meant to be instantiated");
	}

	public static int[] sort(int[] data) {
		for (int index = 1; index < data.length; index++) {
			final int insertionPoint = binarySearch(data, data[index], 0, index);
			if (insertionPoint < index) {
				final int temp = data[index];

				/*
				 * Shift all elements from <code>insertionPoint</code> to
				 * <code>index</code> one position to the right
				 */
				System.arraycopy(data, insertionPoint, data, insertionPoint + 1, index - insertionPoint);
				data[insertionPoint] = temp;
			}
		}

		return data;
	}

	static int binarySearch(int[] data, int key, int from, int to) {
		int start = from, end = to;
		int mid = (to - from) / 2 + from;
		int value = data[mid];

		while (start < end & value != key) {
			if (key > value) {
				start = mid + 1;
			} else {
				end = mid;
			}
			mid = (end - start) / 2 + start;
			value = data[mid];
		}

		return mid;
	}

	/*
	 * Replaced with iterative binary search, because it's faster
	 */
	static int recursiveBinarySearch(int[] data, int key, int from, int to) {
		if (from == to) {
			return from;
		}

		/*
		 * int mid = (from + to) / 2; can overflow
		 */
		int mid = (to - from) / 2 + from;

		if (key > data[mid]) {
			return recursiveBinarySearch(data, key, mid + 1, to);
		} else if (key < data[mid]) {
			return recursiveBinarySearch(data, key, from, mid);
		}

		return mid;
	}

}
