package com.github.svetlinzarev.playground.algorithm.rmq.trivial;

import java.util.Arrays;
import java.util.Objects;

import com.github.svetlinzarev.playground.algorithm.rmq.RangeMinimumQuery;

public class TrivialRMQApproach implements RangeMinimumQuery {
    @Override
    public String toString() {
        return "TrivialRMQApproach [index=" + Arrays.toString(index) + ", data=" + Arrays.toString(data) + "]";
    }

    private final int[][] index;
    private final int[] data;

    public TrivialRMQApproach(int[] data) {
        this.data = Objects.requireNonNull(data);
        index = new int[data.length][data.length];

        for (int i = 0; i < data.length; i++) {
            index[i][i] = i;
        }

        for (int i = 0; i < data.length; i++) {
            for (int j = i + 1; j < data.length; j++) {
                if (data[index[i][j - 1]] < data[j]) {
                    index[i][j] = index[i][j - 1];
                } else {
                    index[i][j] = j;
                }
            }
        }
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
        return index[from][to];
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
        TrivialRMQApproach other = (TrivialRMQApproach) obj;
        if (!Arrays.equals(data, other.data)) {
            return false;
        }
        if (!Arrays.deepEquals(index, other.index)) {
            return false;
        }
        return true;
    }

}
