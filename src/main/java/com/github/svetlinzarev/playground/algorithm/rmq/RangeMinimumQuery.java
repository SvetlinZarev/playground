package com.github.svetlinzarev.playground.algorithm.rmq;

public interface RangeMinimumQuery {
    int queryValue(int from, int to);

    int queryIndex(int from, int to);
}
