package com.github.svetlinzarev.playground.util.cache;


import java.util.Objects;

public class CacheLoaderException extends Exception {

    public CacheLoaderException(Throwable cause) {
        super(Objects.requireNonNull(cause));
    }

    public CacheLoaderException(String message, Throwable cause) {
        super(Objects.requireNonNull(message), Objects.requireNonNull(cause));
    }
}
