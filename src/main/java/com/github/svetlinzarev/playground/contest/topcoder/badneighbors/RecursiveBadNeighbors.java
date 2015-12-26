package com.github.svetlinzarev.playground.contest.topcoder.badneighbors;

import static java.lang.Math.max;


public class RecursiveBadNeighbors implements BadNeighbors {
    private final int[] donations;

    public RecursiveBadNeighbors(int[] donations) {
        this.donations = donations;
    }

    @Override
    public int maxDonations() {
        return max(
                maxDonations(0, donations.length - 1),
                maxDonations(1, donations.length)
        );
    }


    private int maxDonations(int start, int end) {
        int currentMax = 0;
        if (start < end) {
            currentMax = max(
                    maxDonations(start + 2, end),
                    maxDonations(start + 3, end)
            );

            currentMax += donations[start];
        }

        return currentMax;
    }
}
