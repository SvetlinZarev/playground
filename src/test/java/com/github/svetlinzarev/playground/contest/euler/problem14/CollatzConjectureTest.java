package com.github.svetlinzarev.playground.contest.euler.problem14;

import org.junit.Assert;
import org.junit.Test;

public class CollatzConjectureTest {

    @Test
    public void testNextInSequence() {
        Assert.assertEquals(1, CollatzConjecture.nextInSequence(2));
        Assert.assertEquals(10, CollatzConjecture.nextInSequence(3));
        Assert.assertEquals(2, CollatzConjecture.nextInSequence(4));
        Assert.assertEquals(16, CollatzConjecture.nextInSequence(5));
    }

}
