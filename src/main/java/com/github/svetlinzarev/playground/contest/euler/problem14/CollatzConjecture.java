package com.github.svetlinzarev.playground.contest.euler.problem14;

import com.github.svetlinzarev.playground.util.stopwatch.Stopwatch;

/*
 * The following iterative sequence is defined for the set of positive integers:
 * n → n/2 (n is even) n → 3n + 1 (n is odd) Using the rule above and starting
 * with 13, we generate the following sequence: 13 → 40 → 20 → 10 → 5 → 16 → 8 →
 * 4 → 2 → 1 It can be seen that this sequence (starting at 13 and finishing at
 * 1) contains 10 terms. Although it has not been proved yet (Collatz Problem),
 * it is thought that all starting numbers finish at 1. Which starting number,
 * under one million, produces the longest chain?
 */
public class CollatzConjecture {
    private static final int MAX_N = 1_000_000;

    public static void main(String[] args) {
        int[] cache = new int[MAX_N + 1];
        int longestSequenceNumber = 0;
        int longestSequenceLength = 0;

        Stopwatch stopwatch = new Stopwatch();
        for (int sequence = 1, length = 1; sequence <= MAX_N; sequence++, length = 0) {
            long currentSequence = sequence;

            while (!isComputed(currentSequence) && !isCached(currentSequence, sequence)) {
                currentSequence = nextInSequence(currentSequence);
                length += 1;
            }

            length += cache[(int) currentSequence];
            cache[sequence] = length;

            if (length > longestSequenceLength) {
                longestSequenceLength = length;
                longestSequenceNumber = sequence;
            }
        }

        stopwatch.log();
        System.out.println("The starting number " + longestSequenceNumber + " produces a sequence with length "
                + longestSequenceLength);
    }

    static boolean isComputed(long value) {
        return 1 == value;
    }

    static boolean isCached(long value, int sequence) {
        return value < sequence;
    }

    static long nextInSequence(long number) {
        return ((number & 1) == 0) ? (number >> 1) : (number * 3 + 1);
    }

}
