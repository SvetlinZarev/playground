package com.github.svetlinzarev.playground.util.cache.single;


import com.github.svetlinzarev.playground.util.cache.CacheLoaderException;

public interface SingleValueCache<T> {
    interface Loader<T> {
        T loadValue() throws CacheLoaderException;
    }

    T getCachedValue() throws CacheLoaderException;
}
