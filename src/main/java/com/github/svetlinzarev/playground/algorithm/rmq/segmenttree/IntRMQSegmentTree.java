package com.github.svetlinzarev.playground.algorithm.rmq.segmenttree;

import java.util.Arrays;
import java.util.Objects;

import com.github.svetlinzarev.playground.algorithm.rmq.RangeMinimumQuery;

public class IntRMQSegmentTree implements RangeMinimumQuery {
    final int[] data;
    final int[] tree;

    public IntRMQSegmentTree(int[] data) {
        this.data = Objects.requireNonNull(data);
        tree = new int[treeSize(data.length)];

        buildTree(1, 0, data.length - 1);
    }

    private void buildTree(int node, int begin, int end) {
        if (begin == end) {
            tree[node] = begin;
        } else {
            int mid = mid(begin, end);
            int leftNode = left(node);
            int rightNode = right(node);

            // build left sub-tree
            buildTree(leftNode, begin, mid);
            // build right sub-tree
            buildTree(rightNode, mid + 1, end);

            // find the minimum data value in the left and right sub-trees
            if (data[tree[leftNode]] <= data[tree[rightNode]]) {
                tree[node] = tree[leftNode];
            } else {
                tree[node] = tree[rightNode];
            }
        }
    }

    static int treeSize(int dataSize) {
        return 2 * (int) Math.pow(2, treeHeight(dataSize));
    }

    static int treeHeight(int dataSize) {
        return (int) Math.ceil(Math.log(dataSize) / Math.log(2));
    }

    static int left(int node) {
        return 2 * node;
    }

    static int right(int node) {
        return 2 * node + 1;
    }

    static int mid(int begin, int end) {
        return (begin + end) / 2;
    }

    @Override
    public int queryValue(int from, int to) {
        return data[queryIndex(from, to)];
    }

    @Override
    public int queryIndex(int from, int to) {
        if (from > to) {
            throw new IllegalArgumentException("Invalid query boudaries: from=" + from + ", to=" + to);
        }

        // the interval assigned to the first node is from 0 to N-1
        return query(1, 0, data.length - 1, from, to);
    }

    private int query(int node, int begin, int end, int intervalStart, int intervalEnd) {
        int result = -1;

        // Check if the two intervals intersect each other
        if (intervalStart <= end && intervalEnd >= begin) {
            // check if the current interval is included in the query interval
            if (begin >= intervalStart && end <= intervalEnd) {
                result = tree[node];
            } else {
                // get the minimum data value index from the left and right
                // subtrees
                int mid = mid(begin, end);
                int minLeft = query(left(node), begin, mid, intervalStart, intervalEnd);
                int minRight = query(right(node), mid + 1, end, intervalStart, intervalEnd);

                // return the index of the overall minimum
                if (-1 == minLeft) {
                    result = minRight;
                } else if (-1 == minRight) {
                    result = minLeft;
                } else if (data[minLeft] <= data[minRight]) {
                    result = minLeft;
                } else {
                    result = minRight;
                }
            }
        }
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        result = prime * result + Arrays.hashCode(tree);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        IntRMQSegmentTree other = (IntRMQSegmentTree) obj;
        if (!Arrays.equals(data, other.data)) {
            return false;
        }
        if (!Arrays.equals(tree, other.tree)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "IntRMQSegmentTree [data=" + Arrays.toString(data) + ", tree=" + Arrays.toString(tree) + "]";
    }

}
