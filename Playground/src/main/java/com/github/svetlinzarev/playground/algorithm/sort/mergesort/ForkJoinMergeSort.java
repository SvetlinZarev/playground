package com.github.svetlinzarev.playground.algorithm.sort.mergesort;

import java.util.concurrent.RecursiveTask;

public class ForkJoinMergeSort extends RecursiveTask<int[]> {
    private static final long serialVersionUID = 1L;
    private static final int SEQUENTIAL_THRESHOLD = 128;

    private final int[] data;
    private final int[] helper;
    private final int from, to;

    public ForkJoinMergeSort(int[] data) {
        this(data, new int[data.length], 0, data.length - 1);
    }

    private ForkJoinMergeSort(int[] data, int[] helper, int from, int to) {
        this.data = data;
        this.helper = helper;
        this.from = from;
        this.to = to;
    }

    @Override
    protected int[] compute() {
        if (to - from < SEQUENTIAL_THRESHOLD) {
            RecursiveMergeSort.sort(data, helper, from, to);
        } else {
            final int mid = (to - from) / 2 + from;

            final ForkJoinMergeSort left = new ForkJoinMergeSort(data, helper, from, mid);
            final ForkJoinMergeSort right = new ForkJoinMergeSort(data, helper, mid + 1, to);

            left.fork();
            right.compute();
            left.join();

            MergeSortUtil.merge(data, helper, from, mid, to);
        }

        return data;
    }
}
