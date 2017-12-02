package com.github.svetlinzarev.playground.util.cache.single;


import com.github.svetlinzarev.playground.util.cache.CacheLoaderException;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SyncLoadingSingleValueCacheTest {

    private static final int NEGATIVE_TTL = 100;
    private static final int POSITIVE_TTL = 50;

    @Test
    public void singleThreadedNegativeCacheTest() throws Exception {
        final AtomicInteger reloadsCounter = new AtomicInteger();
        final SingleValueCache.Loader<String> loader = () -> {
            throw new CacheLoaderException("Expected from test " + reloadsCounter.getAndIncrement(), new RuntimeException());
        };

        final SingleValueCache<String> cache = new SyncLoadingSingleValueCache<>(loader, POSITIVE_TTL, NEGATIVE_TTL);

        Exception lastFailure = null, previousFailure = null;
        for (long start = System.currentTimeMillis(), now = start; now - start < NEGATIVE_TTL; now = System.currentTimeMillis()) {
            try {
                cache.getCachedValue();
                fail("Should have thrown CacheLoaderException.");
            } catch (CacheLoaderException ex) {
                lastFailure = ex;
            }

            if (null != previousFailure) {
                assertSame(previousFailure.getCause(), lastFailure.getCause());
                assertNotSame(previousFailure, lastFailure);
            }

            previousFailure = lastFailure;
        }

        assertEquals(1, reloadsCounter.get());
    }

    @Test
    public void singleThreadedNegativeCacheExpirationTest() throws Exception {
        final int expectedNumberOfReloads = 5;

        final AtomicInteger reloadsCounter = new AtomicInteger();
        final SingleValueCache.Loader<String> loader = () -> {
            throw new CacheLoaderException("Expected from test " + reloadsCounter.getAndIncrement(), new RuntimeException());
        };

        final SingleValueCache<String> cache = new SyncLoadingSingleValueCache<>(loader, POSITIVE_TTL, NEGATIVE_TTL);

        int rootCauseCounter = 1;
        Exception lastFailure = null, previousFailure = null;
        for (long start = System.currentTimeMillis(), now = start; now - start < NEGATIVE_TTL * expectedNumberOfReloads; now = System.currentTimeMillis()) {
            try {
                cache.getCachedValue();
                fail("Should have thrown CacheLoaderException.");
            } catch (CacheLoaderException ex) {
                lastFailure = ex;
            }

            if (null != previousFailure) {
                if (previousFailure.getCause() != lastFailure.getCause()) {
                    rootCauseCounter++;
                }
            }

            previousFailure = lastFailure;
        }

        assertEquals(expectedNumberOfReloads, reloadsCounter.get());
        assertEquals(expectedNumberOfReloads, rootCauseCounter);
    }

}
