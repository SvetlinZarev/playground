package com.github.svetlinzarev.playground.algorithm.rmq.segmenttree;

import org.junit.Assert;
import org.junit.Test;

import com.github.svetlinzarev.playground.algorithm.rmq.RangeMinimumQueryTestBase;
import com.github.svetlinzarev.playground.algorithm.rmq.segmenttree.IntRMQSegmentTree;

public class IntRMQSegmentTreeTest extends RangeMinimumQueryTestBase {

    @Test
    public void testBuildTree() {
        int[] data = { 3, 5, 1, 7, 2, 4 };
        int expectedTree[] = { 0, 2, 2, 4, 0, 2, 4, 5, 0, 1, 0, 0, 3, 4, 0, 0 };

        IntRMQSegmentTree intRMQSegmentTree = new IntRMQSegmentTree(data);

        Assert.assertArrayEquals(expectedTree, intRMQSegmentTree.tree);
    }

    @Test
    public void testQueryValue() throws Exception {
        IntRMQSegmentTree intRMQSegmentTree = new IntRMQSegmentTree(getData());
        testQueryValue(intRMQSegmentTree);
    }

    @Test
    public void testQueryIndex() throws Exception {
        IntRMQSegmentTree intRMQSegmentTree = new IntRMQSegmentTree(getData());
        testQueryIndex(intRMQSegmentTree);
    }

    @Test
    public void testTreeSsize() throws Exception {
        Assert.assertEquals(4, IntRMQSegmentTree.treeSize(2));
        Assert.assertEquals(8, IntRMQSegmentTree.treeSize(3));
        Assert.assertEquals(8, IntRMQSegmentTree.treeSize(4));
        Assert.assertEquals(16, IntRMQSegmentTree.treeSize(5));
        Assert.assertEquals(16, IntRMQSegmentTree.treeSize(6));
        Assert.assertEquals(16, IntRMQSegmentTree.treeSize(7));
        Assert.assertEquals(16, IntRMQSegmentTree.treeSize(8));
        Assert.assertEquals(32, IntRMQSegmentTree.treeSize(16));
        Assert.assertEquals(32_768, IntRMQSegmentTree.treeSize(12345));
    }

    @Test
    public void testTreeHeight() throws Exception {
        Assert.assertEquals(1, IntRMQSegmentTree.treeHeight(2));
        Assert.assertEquals(2, IntRMQSegmentTree.treeHeight(3));
        Assert.assertEquals(2, IntRMQSegmentTree.treeHeight(4));
        Assert.assertEquals(3, IntRMQSegmentTree.treeHeight(5));
        Assert.assertEquals(3, IntRMQSegmentTree.treeHeight(6));
        Assert.assertEquals(3, IntRMQSegmentTree.treeHeight(7));
        Assert.assertEquals(3, IntRMQSegmentTree.treeHeight(8));
        Assert.assertEquals(4, IntRMQSegmentTree.treeHeight(16));
        Assert.assertEquals(14, IntRMQSegmentTree.treeHeight(12345));
    }

    @Test
    public void testLeft() throws Exception {
        Assert.assertEquals(2, IntRMQSegmentTree.left(1));
        Assert.assertEquals(4, IntRMQSegmentTree.left(2));
        Assert.assertEquals(6, IntRMQSegmentTree.left(3));
    }

    @Test
    public void testRight() throws Exception {
        Assert.assertEquals(3, IntRMQSegmentTree.right(1));
        Assert.assertEquals(5, IntRMQSegmentTree.right(2));
        Assert.assertEquals(7, IntRMQSegmentTree.right(3));
    }

    @Test
    public void testMid() throws Exception {
        Assert.assertEquals(4, IntRMQSegmentTree.mid(0, 8));
        Assert.assertEquals(4, IntRMQSegmentTree.mid(1, 8));
        Assert.assertEquals(5, IntRMQSegmentTree.mid(2, 8));
        Assert.assertEquals(4, IntRMQSegmentTree.mid(2, 7));
        Assert.assertEquals(3, IntRMQSegmentTree.mid(3, 3));
        Assert.assertEquals(5, IntRMQSegmentTree.mid(5, 6));
    }
}
