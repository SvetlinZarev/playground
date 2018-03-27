package com.github.svetlinzarev.playground.contest.euler.problem14;

/*
 * The following iterative sequence is defined for the set of positive integers:
 * n → n/2 (n is even) n → 3n + 1 (n is odd) Using the rule above and starting
 * with 13, we generate the following sequence: 13 → 40 → 20 → 10 → 5 → 16 → 8 →
 * 4 → 2 → 1 It can be seen that this sequence (starting at 13 and finishing at
 * 1) contains 10 terms. Although it has not been proved yet (Collatz Problem),
 * it is thought that all starting numbers finish at 1. Which starting number,
 * under one million, produces the longest chain?
 */
public final class RecursiveCollatzConjecture {
    //All lengths of sequences uo to Integer.MAX_VALUE will fit in a short
    private final short[] cache;

    public RecursiveCollatzConjecture(int cacheSize) {
        if (cacheSize < 2) {
            throw new IllegalArgumentException("Invalid cache size: " + cacheSize);
        }
        this.cache = new short[cacheSize];
        cache[1] = 1;
    }

    public CollatzResult findLongestSequence(long x) {
        if (x <= 1) {
            throw new IllegalArgumentException("Invalid number of sequences; " + x);
        }

        int maxLength = 0;
        long sequenceStart = 0;

        final long start = System.nanoTime();
        for (long i = 1; i <= x && i >= 1; i++) {
            final int sequenceLength = findSequenceLength(i);
            if (maxLength < sequenceLength) {
                sequenceStart = i;
                maxLength = sequenceLength;
            }
        }
        final long end = System.nanoTime();
        return new CollatzResult(Math.abs(end - start) / 1_000_000, sequenceStart, maxLength);
    }

    private short findSequenceLength(long sequenceStart) {
        assert sequenceStart >= 1 : "Invalid sequence start: " + sequenceStart;

        short sequenceLength = getFromCache(sequenceStart);
        if (0 == sequenceLength) {
            long nextInSequence = nextInSequence(sequenceStart);
            while (nextInSequence >= cache.length) {
                nextInSequence = nextInSequence(nextInSequence);
                sequenceLength += 1;
            }
            sequenceLength += findSequenceLength(nextInSequence) + 1;
            cache(sequenceStart, sequenceLength);
        }
        return sequenceLength;
    }

    private short getFromCache(long sequenceStart) {
        assert sequenceStart > 0 : "Invalid sequence start: " + sequenceStart;

        short cachedLength = 0;
        if (sequenceStart < cache.length) {
            cachedLength = cache[(int) sequenceStart];
        }
        return cachedLength;
    }

    public void cache(long sequenceStart, short sequenceLength) {
        assert sequenceStart > 0 : "Invalid sequence start: " + sequenceStart;
        if (sequenceStart < cache.length) {
            cache[(int) sequenceStart] = sequenceLength;
        }
    }

    private long nextInSequence(long sequenceStart) {
        long nextInSequence;
        if ((sequenceStart & 1) == 0) {
            nextInSequence = sequenceStart >> 1;
        } else {
            nextInSequence = sequenceStart * 3 + 1;
        }
        return nextInSequence;
    }

    public static void main(String[] args) {
        //Integer.MAX_VALUE-2 because of https://bugs.openjdk.java.net/browse/JDK-8059914
        final RecursiveCollatzConjecture collatzConjecture = new RecursiveCollatzConjecture(Integer.MAX_VALUE - 2);
        final CollatzResult result = collatzConjecture.findLongestSequence(Integer.MAX_VALUE);
        System.out.println(result);
    }
}

final class CollatzResult {
    private final long time;
    private final long sequenceStart;
    private final long sequenceLength;

    public CollatzResult(long time, long sequenceStart, long sequenceLength) {
        this.time = time;
        this.sequenceStart = sequenceStart;
        this.sequenceLength = sequenceLength;
    }

    public long getTime() {
        return time;
    }

    public long getSequenceStart() {
        return sequenceStart;
    }

    public long getSequenceLength() {
        return sequenceLength;
    }

    @Override
    public String toString() {
        return "CollatzResult{" +
                "time=" + time +
                ", sequenceStart=" + sequenceStart +
                ", sequenceLength=" + sequenceLength +
                '}';
    }
}
