package com.github.svetlinzarev.playground.util.pool;


public interface Pool<T> {
    interface Factory<T> {
        T createInstance();
    }

    interface Recycler<T> {
        T recycle(T instance);
    }

    T borrowInstance();

    void returnInstance(T instance);
}
