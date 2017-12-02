package com.github.svetlinzarev.playground.util.pool;

import com.github.svetlinzarev.playground.util.pool.Pool.Factory;
import com.github.svetlinzarev.playground.util.pool.Pool.Recycler;
import org.junit.Test;
import org.junit.jupiter.api.Tag;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class ConcurrentPoolTest {
    private static final int ITERATIONS_PER_THREAD = 250;
    private static final int MIN_SLEEP = 5;
    private static final int MAX_SLEEP = 15;

    private static final double EXPECTED_MIN_HIT_RATE = 0.95d;
    private static final double MAX_HITRATE_DELTA = 0.01d;

    @Tag("slow")
    @Test
    public void testHitRate() throws Exception {
        final AtomicInteger retrievals = new AtomicInteger();
        final AtomicInteger creations = new AtomicInteger();

        final Recycler<Object> recycler = instance -> {
            retrievals.incrementAndGet();
            return instance;
        };

        final Factory<Object> factory = () -> {
            creations.incrementAndGet();
            return this;
        };

        final int availableProcessors = Runtime.getRuntime().availableProcessors();
        final int capacityHint = Math.max(8, availableProcessors * 2);
        final Pool<Object> pool = new ConcurrentPool<>(factory, recycler, capacityHint);

        final int nThreads = availableProcessors * 32;
        final CountDownLatch startBarrier = new CountDownLatch(1);
        final CountDownLatch finishBarrier = new CountDownLatch(nThreads);

        final ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
        for (int i = 0; i < nThreads; i++) {
            executorService.execute(() -> {
                try {
                    final Random random = new Random();
                    startBarrier.await();
                    for (int iterations = 0; iterations < ITERATIONS_PER_THREAD; iterations++) {
                        final Object instance = pool.borrowInstance();
                        sleep(MIN_SLEEP + random.nextInt(MAX_SLEEP - MIN_SLEEP));
                        pool.returnInstance(instance);
                        sleep(MIN_SLEEP + random.nextInt(MAX_SLEEP - MIN_SLEEP));
                    }
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                } finally {
                    finishBarrier.countDown();
                }
            });
        }

        startBarrier.countDown();
        finishBarrier.await();
        executorService.shutdown();

        final double hitRate = (retrievals.intValue() - creations.intValue()) / (double) retrievals.intValue();
        assertTrue((EXPECTED_MIN_HIT_RATE - hitRate) <= MAX_HITRATE_DELTA, "Expecting hit rate greater than " + EXPECTED_MIN_HIT_RATE + "; Actual: " + hitRate);
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            //no-op
        }
    }
}

