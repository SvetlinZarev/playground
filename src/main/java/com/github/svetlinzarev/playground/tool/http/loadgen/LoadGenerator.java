package com.github.svetlinzarev.playground.tool.http.loadgen;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;

public class LoadGenerator {
    private static final byte[] DISCARD_BUFFER = new byte[1024 * 8];

    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        final int numberOfRequests = Integer.parseInt(args[0]);
        final int concurrencyLevel = Integer.parseInt(args[1]);
        final URL urlAddress = new URL(args[2]);

        final AtomicInteger numberOfSuccessfulRequests = new AtomicInteger();
        final AtomicInteger numberOfFailedRequests = new AtomicInteger();
        final AtomicInteger deltaSuccessfulRequests = new AtomicInteger();
        final AtomicInteger deltaFailedRequests = new AtomicInteger();
        final AtomicLong lastStatisticsUpdateTime = new AtomicLong(System.currentTimeMillis());
        final AtomicLong transferredBytes = new AtomicLong();
        final AtomicInteger deltaTransferredData = new AtomicInteger();

        final ExecutorService executor = Executors.newFixedThreadPool(concurrencyLevel);
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfRequests);

        //warm up the executor
        for (int i = 0; i < 80; i++) {
            executor.execute(() -> System.out.print("#"));
        }

        final long startTotalTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfRequests; i++) {
            executor.execute(() -> {
                try {
                    final HttpURLConnection connection = (HttpURLConnection) urlAddress.openConnection();
                    connection.setRequestProperty("Accept-Encoding", "gzip");
                    connection.setRequestProperty("User-Agent", "Simple Load Runner");
                    connection.setUseCaches(false);
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(15000);

                    try (InputStream inputStream = getResponseStream(connection)) {
                        final int bytesRead = consumeStream(inputStream);
                        transferredBytes.addAndGet(bytesRead);
                        deltaTransferredData.addAndGet(bytesRead);
                    }

                    if (connection.getResponseCode() >= 400) {
                        numberOfFailedRequests.incrementAndGet();
                        deltaFailedRequests.incrementAndGet();
                    } else {
                        numberOfSuccessfulRequests.incrementAndGet();
                        deltaSuccessfulRequests.incrementAndGet();
                    }
                } catch (Exception exception) {
                    numberOfFailedRequests.incrementAndGet();
                    deltaFailedRequests.incrementAndGet();
                    System.err.println(exception);
                } finally {
                    countDownLatch.countDown();
                }

                long deltaTime = System.currentTimeMillis() - lastStatisticsUpdateTime.get();
                if (deltaTime >= 1000) {
                    synchronized (LoadGenerator.class) {
                        deltaTime = System.currentTimeMillis() - lastStatisticsUpdateTime.get();
                        if (deltaTime > 1000) {
                            lastStatisticsUpdateTime.set(System.currentTimeMillis());
                            final int deltaSuccessful = deltaSuccessfulRequests.getAndSet(0);
                            final int deltaFailed = deltaFailedRequests.getAndSet(0);
                            final long deltaBytes = deltaTransferredData.getAndSet(0);

                            printStatistics(deltaSuccessful, deltaFailed, deltaTime, deltaBytes);
                        }
                    }
                }
            });
        }

        executor.shutdown();
        countDownLatch.await();

        final long totalTimeMillis = System.currentTimeMillis() - startTotalTime;
        printStatistics(numberOfSuccessfulRequests.get(), numberOfFailedRequests.get(), totalTimeMillis, transferredBytes.get());
    }

    private static double toSeconds(long millis) {
        return millis / 1_000.0;
    }

    private static InputStream getResponseStream(HttpURLConnection connection) throws IOException {
        final InputStream inputStream;
        final int responseCode = connection.getResponseCode();
        if (responseCode >= 400) {
            inputStream = connection.getErrorStream();
        } else {
            inputStream = connection.getInputStream();
        }
        return inputStream;
    }

    private static int consumeStream(InputStream inputStream) throws IOException {
        int transferredBytes = 0;
        if (null != inputStream) {
            for (int bytesRead = inputStream.read(DISCARD_BUFFER); bytesRead != -1; bytesRead = inputStream.read(DISCARD_BUFFER)) {
                transferredBytes += bytesRead;
            }
        }
        return transferredBytes;
    }

    private static void printStatistics(int successfulRequests, int failedRequests, long timeMillis, long transferredBytes) {
        final double totalTimeSec = toSeconds(timeMillis);
        System.out.println("\n################################################################################");
        System.out.println("Successful requests: " + successfulRequests);
        System.out.println("Failed requests: " + failedRequests);
        System.out.println(format("Clock time [sec]: %.2f", totalTimeSec));
        System.out.println(format("Successful requests/second: %.2f", successfulRequests / totalTimeSec));
        System.out.println(format("Failed requests/second: %.2f", failedRequests / totalTimeSec));
        System.out.println(format("KB/second: %.2f", (transferredBytes / 1024.0) / totalTimeSec));
        System.out.println("################################################################################");
    }
}
