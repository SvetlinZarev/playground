package com.github.svetlinzarev.playground.util.pool;

import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.abs;


public final class ConcurrentPool<T> implements Pool<T> {
    private static final int MIN_BUCKETS = 4;
    private final AtomicInteger bucketSelector;
    private final Factory<T> objectFactory;
    private final Recycler<T> objectRecycler;
    private final BlockingQueue<T>[] buckets;

    public ConcurrentPool(Factory<T> objectFactory, Recycler<T> objectRecycler, int capacityHint) {
        if (capacityHint <= 0) {
            throw new IllegalArgumentException("The capacity hint cannot be less than 1. Current value: " + capacityHint);
        }

        this.objectFactory = Objects.requireNonNull(objectFactory);
        this.objectRecycler = Objects.requireNonNull(objectRecycler);
        this.bucketSelector = new AtomicInteger();

        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        buckets = new BlockingQueue[Math.max(MIN_BUCKETS, availableProcessors * 2)];
        final int capacityPerBucket = 1 + capacityHint / buckets.length;
        for (int i = 0; i < buckets.length; i++) {
            buckets[i] = new ArrayBlockingQueue<>(capacityPerBucket);
        }
    }

    @Override
    public T borrowInstance() {
        final int bucket = selectBucketOnRetrieval();
        T instance = buckets[bucket].poll();

        if (null == instance) {
            instance = objectFactory.createInstance();
        }

        return instance;
    }

    private int selectBucketOnRetrieval() {
        return abs(bucketSelector.getAndIncrement()) % buckets.length;
    }

    @Override
    public void returnInstance(T instance) {
        final T obj = objectRecycler.recycle(instance);
        if (null != obj) {
            final int bucket = selectBucketOnReturn();
            buckets[bucket].offer(instance);
        }
    }

    private int selectBucketOnReturn() {
        return abs(bucketSelector.decrementAndGet()) % buckets.length;
    }
}
