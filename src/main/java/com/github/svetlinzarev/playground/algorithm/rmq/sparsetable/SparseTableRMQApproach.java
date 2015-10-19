package com.github.svetlinzarev.playground.algorithm.rmq.sparsetable;

import java.util.Arrays;
import java.util.Objects;

import com.github.svetlinzarev.playground.algorithm.rmq.RangeMinimumQuery;

public class SparseTableRMQApproach implements RangeMinimumQuery {
    private final int[] data;
    private final int[][] index;

    public SparseTableRMQApproach(int[] data) {
        this.data = Objects.requireNonNull(data);
        index = new int[data.length][log2(data.length + 1)];

        for (int i = 0; i < data.length; i++) {
            index[i][0] = i;
        }

        for (int j = 1; 1 << j <= data.length; j++) {
            for (int i = 0; i + (1 << j) - 1 < data.length; i++) {
                if (data[index[i][j - 1]] < data[index[i + (1 << (j - 1))][j - 1]]) {
                    index[i][j] = index[i][j - 1];
                } else {
                    index[i][j] = index[i + (1 << (j - 1))][j - 1];
                }
            }
        }

    }

    private static int log2(int x) {
        return (int) Math.ceil((Math.log(x) / Math.log(2)));
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

        int k = (int) Math.log(to - from + 1);
        if (data[index[from][k]] <= data[index[to - (1 << k) + 1][k]]) {
            return index[from][k];
        } else {
            return index[to - (1 << k) + 1][k];
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        result = prime * result + Arrays.hashCode(index);
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
        SparseTableRMQApproach other = (SparseTableRMQApproach) obj;
        if (!Arrays.equals(data, other.data)) {
            return false;
        }
        if (!Arrays.deepEquals(index, other.index)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SparseTableRMQApproach [data=" + Arrays.toString(data) + ", index=" + Arrays.toString(index) + "]";
    }

}
