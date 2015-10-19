package com.github.svetlinzarev.playground.algorithm.rmq.sqrtarray;

import java.util.Arrays;
import java.util.Objects;

import com.github.svetlinzarev.playground.algorithm.rmq.RangeMinimumQuery;

public class SqrtArrayRMQApproach implements RangeMinimumQuery {
    private final int[] data;
    private final int[] index;
    private final int partLength;

    public SqrtArrayRMQApproach(int[] data) {
        this.data = Objects.requireNonNull(data);
        partLength = (int) Math.sqrt(data.length);
        index = new int[(int) Math.ceil(data.length / (double) partLength)];

        for (int i = 0; i < index.length; i++) {
            index[i] = i * partLength;
        }

        for (int i = 0; i < data.length; i++) {
            int pIndex = i / partLength;
            index[pIndex] = minIndex(i, pIndex);
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

        int firstPart = from / partLength;
        int lastPart = (to - 1) / partLength;
        int minValueIndex = from;

        if (firstPart == lastPart) {
            for (int i = from; i < to; i++) {
                minValueIndex = minIndex(minValueIndex, i);
            }
        } else {
            for (int i = firstPart; i < partLength * (firstPart + 1); i++) {
                minValueIndex = minIndex(minValueIndex, i);
            }

            for (int i = firstPart + 1; i < lastPart; i++) {
                minValueIndex = minIndex(minValueIndex, index[i]);
            }

            for (int i = lastPart * partLength; i < to; i++) {
                minValueIndex = minIndex(minValueIndex, i);
            }
        }

        return minValueIndex;
    }

    private int minIndex(int a, int b) {
        return data[a] < data[b] ? a : b;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(data);
        result = prime * result + Arrays.hashCode(index);
        result = prime * result + partLength;
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
        SqrtArrayRMQApproach other = (SqrtArrayRMQApproach) obj;
        if (!Arrays.equals(data, other.data)) {
            return false;
        }
        if (!Arrays.equals(index, other.index)) {
            return false;
        }
        if (partLength != other.partLength) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SqrtArrayRMQApproach [data=" + Arrays.toString(data) + ", index=" + Arrays.toString(index)
                + ", partLength=" + partLength + "]";
    }

}
