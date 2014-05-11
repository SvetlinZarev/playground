package com.github.svetlinzarev.playground.algorithm.arrays;

import java.util.Arrays;

import com.github.svetlinzarev.playground.util.array.Array;
import com.github.svetlinzarev.playground.util.stopwatch.Stopwatch;

public class LongestIncreasingSubsequence {
    private static final int ARRAY_LENGTH = 1000;

    private LongestIncreasingSubsequence() {
        // prevent instantiation
    }

    public static void main(String[] args) {
        int[] array = Array.random(ARRAY_LENGTH, 0, ARRAY_LENGTH);
        if (array.length <= 10) {
            System.out.println(Arrays.toString(array));
        }

        Stopwatch stopwatch = new Stopwatch();
        int dynProgLength = dynamicProgrammingApproach(array);
        stopwatch.log("Dynamic Programming Approach");
        System.out.println("Length=" + dynProgLength);
    }

    public static int dynamicProgrammingApproach(int[] data) {
        int[] best = new int[data.length];

        for (int i = 0; i < data.length; i++) {
            int max = 0;

            for (int j = 0; j < i; j++) {
                if (data[i] > data[j]) {
                    if (best[j] > max) {
                        max = best[j];
                    }
                }
            }

            best[i] = max + 1;
        }

        int longest = 0;
        for (int element : best) {
            longest = Math.max(longest, element);
        }
        return longest;
    }
}
