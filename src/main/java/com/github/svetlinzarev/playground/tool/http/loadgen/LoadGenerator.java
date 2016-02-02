package com.github.svetlinzarev.playground.tool.http.loadgen;


import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;
import static java.net.Proxy.Type.HTTP;

public class LoadGenerator {
    private static final byte[] DISCARD_BUFFER = new byte[1024 * 8];

    public static void main(String[] args) throws Exception {
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

        final Proxy proxy = getProxyConfiguration(urlAddress);
        final ExecutorService executor = Executors.newFixedThreadPool(concurrencyLevel);
        final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        final CountDownLatch countDownLatch = new CountDownLatch(numberOfRequests);

        //warm up the executor
        for (int i = 0; i < 80; i++) {
            executor.execute(() -> System.out.print("#"));
        }

        final long startTotalTime = System.currentTimeMillis();
        for (int i = 0; i < numberOfRequests; i++) {
            executor.execute(() -> {
                try {
                    final HttpURLConnection connection = (HttpURLConnection) urlAddress.openConnection(proxy);
                    connection.setRequestProperty("Accept-Encoding", "gzip");
                    connection.setRequestProperty("User-Agent", "Simple Load Runner");
                    connection.setUseCaches(false);
                    connection.setConnectTimeout(15000);
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
            });
        }

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            final long deltaTime = System.currentTimeMillis() - lastStatisticsUpdateTime.getAndSet(System.currentTimeMillis());
            final int deltaSuccessful = deltaSuccessfulRequests.getAndSet(0);
            final int deltaFailed = deltaFailedRequests.getAndSet(0);
            final long deltaBytes = deltaTransferredData.getAndSet(0);
            printStatistics(deltaSuccessful, deltaFailed, deltaTime, deltaBytes);
        }, 0, 1_000, TimeUnit.MILLISECONDS);

        countDownLatch.await();
        executor.shutdown();
        scheduledExecutorService.shutdown();

        final long totalTimeMillis = System.currentTimeMillis() - startTotalTime;
        printStatistics(numberOfSuccessfulRequests.get(), numberOfFailedRequests.get(), totalTimeMillis, transferredBytes.get());
    }

    private static Proxy getProxyConfiguration(URL address) throws URISyntaxException {
        Proxy selectedProxy = Proxy.NO_PROXY;
        final ProxySelector proxySelector = ProxySelector.getDefault();
        for (Proxy proxy : proxySelector.select(address.toURI())) {
            if (proxy.type().equals(HTTP)) {
                selectedProxy = proxy;
                break;
            }
        }

        System.out.println("### Proxy: " + selectedProxy);
        return selectedProxy;
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

    private static double toSeconds(long millis) {
        return millis / 1_000.0;
    }

    private static synchronized void printStatistics(int successfulRequests, int failedRequests, long timeMillis, long transferredBytes) {
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

