package com.github.svetlinzarev.playground.util.cache.single;


import com.github.svetlinzarev.playground.util.cache.CacheLoaderException;

import java.util.Objects;

public final class SyncLoadingSingleValueCache<T> implements SingleValueCache<T> {
    private final Loader<T> cacheLoader;
    private final long positiveCacheMillis;
    private final long circuitBreakerMillis;

    private volatile Exception failure;
    private volatile T value;
    private long lastReload;

    public SyncLoadingSingleValueCache(Loader<T> cacheLoader, long positiveCacheMillis, long circuitBreakerMillis) {
        this.cacheLoader = Objects.requireNonNull(cacheLoader);
        this.positiveCacheMillis = positiveCacheMillis;
        this.circuitBreakerMillis = circuitBreakerMillis;

        if (positiveCacheMillis <= 0) {
            throw new IllegalArgumentException("Invalid positive TTL: " + positiveCacheMillis);
        }

        if (circuitBreakerMillis < 0) {
            throw new IllegalArgumentException("Invalid circuit breaker timeout: " + circuitBreakerMillis);
        }
    }


    //TODO is it possible to not throw circuit breaker exception, but also not to reload cache ?
    @Override
    public T getCachedValue() throws CacheLoaderException {
        final long now = System.currentTimeMillis();
        final long elapsedMillisSinceLastReloadAttempt = now - lastReload;
        final Exception lastFailure = failure;

        //If the circuit breaker is open
        if (null != lastFailure & elapsedMillisSinceLastReloadAttempt < circuitBreakerMillis & elapsedMillisSinceLastReloadAttempt >= 0) {
            throw new CacheLoaderException("Circuit breaker is open due to: " + lastFailure.getMessage(), lastFailure);
        }

        /*
           Reload either because of the negative or positive caches have expired
         */
        if (null != lastFailure | elapsedMillisSinceLastReloadAttempt > positiveCacheMillis) {
            synchronized (this) {
                if (null != lastFailure | now - lastReload > positiveCacheMillis) {
                    try {
                        System.out.println("reloading");
                        value = cacheLoader.loadValue();
                        failure = null;
                    } catch (Exception ex) {
                        failure = ex;
                        throw new CacheLoaderException("Failed to reload cached value: ", ex);
                    } finally {
                        lastReload = System.currentTimeMillis();
                    }
                }
            }
        }

        return value;
    }

}
