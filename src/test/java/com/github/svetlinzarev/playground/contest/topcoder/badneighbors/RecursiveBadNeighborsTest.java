package com.github.svetlinzarev.playground.contest.topcoder.badneighbors;

import org.junit.Assert;
import org.junit.Test;


public class RecursiveBadNeighborsTest {

    @Test
    public void testMaxDonationsCaseA() throws Exception {
        final int expected = 19;
        final int[] donations = {10, 3, 2, 5, 7, 8};
        BadNeighbors badNeighbors = new RecursiveBadNeighbors(donations);
        Assert.assertEquals(expected, badNeighbors.maxDonations());
    }

    @Test
    public void testMaxDonationsCaseB() throws Exception {
        final int expected = 15;
        final int[] donations = {11, 15};
        BadNeighbors badNeighbors = new RecursiveBadNeighbors(donations);
        Assert.assertEquals(expected, badNeighbors.maxDonations());
    }

    @Test
    public void testMaxDonationsCaseC() throws Exception {
        final int expected = 21;
        final int[] donations = {7, 7, 7, 7, 7, 7, 7};
        BadNeighbors badNeighbors = new RecursiveBadNeighbors(donations);
        Assert.assertEquals(expected, badNeighbors.maxDonations());
    }

    @Test
    public void testMaxDonationsCaseD() throws Exception {
        final int expected = 2926;
        final int[] donations = {94, 40, 49, 65, 21, 21, 106, 80, 92, 81, 679, 4, 61,
                6, 237, 12, 72, 74, 29, 95, 265, 35, 47, 1, 61, 397,
                52, 72, 37, 51, 1, 81, 45, 435, 7, 36, 57, 86, 81, 72};
        BadNeighbors badNeighbors = new RecursiveBadNeighbors(donations);
        Assert.assertEquals(expected, badNeighbors.maxDonations());
    }
}