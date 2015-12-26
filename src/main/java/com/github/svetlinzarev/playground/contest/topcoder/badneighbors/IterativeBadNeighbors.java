package com.github.svetlinzarev.playground.contest.topcoder.badneighbors;

public class IterativeBadNeighbors implements BadNeighbors {
    private final int[] donations;

    public IterativeBadNeighbors(int[] donations) {
        this.donations = donations;
    }

    @Override
    public int maxDonations() {
        return Math.max(
                maxDonations(0, donations.length - 1),
                maxDonations(1, donations.length)
        );
    }

    private int maxDonations(int start, int end) {
        assert start == 0 || start == 1 : "The start index must be either 0 or 1";
        assert start <= end : "The start index must not be greater than the end index";

        int currentMaximum = donations[start];
        for (int i = start + 1, prevMaximum = 0; i < end; i++) {
            final int nextMaximum = Math.max(donations[i] + prevMaximum, currentMaximum);
            prevMaximum = currentMaximum;
            currentMaximum = nextMaximum;
        }
        return currentMaximum;
    }
}
