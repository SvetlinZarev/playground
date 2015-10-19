package com.github.svetlinzarev.playground.util.stopwatch;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Stopwatch implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger TIME_LOGGER = LoggerFactory.getLogger(Stopwatch.class);
    private long startTime;

    public Stopwatch() {
        startTime = System.nanoTime();
    }

    public void reset() {
        startTime = System.nanoTime();
    }

    public long time() {
        final long endTime = System.nanoTime();
        return endTime - startTime;
    }

    public long log() {
        final long time = time();
        TIME_LOGGER.info("Elapsed time [ms] : {} ({} ns)", time / 1000000, time);
        return time;
    }

    public long log(String message) {
        final long time = time();
        TIME_LOGGER.info("({}) Elapsed time [ms] : {} ({} ns)", message, time / 1000000, time);
        return time;
    }
}