package com.github.svetlinzarev.playground.algorithm.sort.mergesort;

public class RecursiveMergeSort {
	private RecursiveMergeSort() {
		throw new AssertionError("This class is not meant to be instantiated");
	}

	public static int[] sort(int[] data) {
		int[] helper = new int[data.length];
		return sort(data, helper, 0, data.length - 1);
	}

	static int[] sort(int[] data, int[] helper, int from, int to) {
		if (from < to) {
			int mid = (to - from) / 2 + from;
			sort(data, helper, from, mid);
			sort(data, helper, mid + 1, to);
			MergeSortUtil.merge(data, helper, from, mid, to);
		}

		return data;
	}
}
