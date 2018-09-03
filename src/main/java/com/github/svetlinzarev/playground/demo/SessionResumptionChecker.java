package com.github.svetlinzarev.playground.demo;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;


public final class SessionResumptionChecker {
    private static final int INDEX_REUSED = 0;
    private static final String SESSION_MARKER = "session-marker";

    private final SSLContext sslContext;

    public static class Result {
        private final Map<String, Integer> sessions;
        private final int reusedSessions;
        private final int allSessions;

        public Result(Map<String, Integer> sessions, int reusedSessions, int allSessions) {
            this.sessions = sessions;
            this.reusedSessions = reusedSessions;
            this.allSessions = allSessions;
        }

        public int getReusedSessions() {
            return this.reusedSessions;
        }

        public int getAllSessions() {
            return this.allSessions;
        }

        public Map<String, Integer> getSessions() {
            return Collections.unmodifiableMap(sessions);
        }

        @Override
        public String toString() {
            return "Result{" +
              "\nsessions=" + sessions +
              "\nreusedSessions=" + reusedSessions + " (" + String.format("%.2f", ((double) reusedSessions / allSessions) * 100) + "%)" +
              "\nallSessions=" + allSessions +
              "\n}";
        }
    }


    public SessionResumptionChecker(String protocol) throws GeneralSecurityException {
        sslContext = SSLContext.getInstance(protocol);
        sslContext.init(null, null, null);
    }

    public Result checkSessionResumption(int iterations, String host, int port) throws IOException {
        final SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        final SSLParameters sslParameters = new SSLParameters();
        sslParameters.setEndpointIdentificationAlgorithm("HTTPS");

        final int[] reusedCounter = new int[1];
        for (int i = 0; i < iterations; i++) {
            final SSLSocket socket = (SSLSocket) socketFactory.createSocket();

            try {
                socket.setUseClientMode(true);
                socket.setSSLParameters(sslParameters);
                socket.addHandshakeCompletedListener(e -> {
                    final SSLSession session = e.getSession();
                    final Integer sessionMarker = (Integer) session.getValue(SESSION_MARKER);
                    if (null == sessionMarker) {
                        session.putValue(SESSION_MARKER, 1);
                    } else {
                        session.putValue(SESSION_MARKER, sessionMarker + 1);
                        reusedCounter[INDEX_REUSED]++;
                    }
                });
                socket.connect(new InetSocketAddress(host, port));
                socket.startHandshake();
                System.out.print(".");
            } finally {
                socket.close();
            }
        }

        final Map<String, Integer> sessions = new HashMap<>();
        final SSLSessionContext clientSessionContext = sslContext.getClientSessionContext();
        final Enumeration<byte[]> ids = clientSessionContext.getIds();

        while (ids.hasMoreElements()) {
            final byte[] id = ids.nextElement();
            final SSLSession session = clientSessionContext.getSession(id);
            final Integer reuseCount = (Integer) session.getValue(SESSION_MARKER);

            sessions.put(sessionIdToString(id), null == reuseCount ? 0 : reuseCount);
        }

        System.out.println();
        return new Result(sessions, reusedCounter[INDEX_REUSED], iterations);
    }

    private String sessionIdToString(byte[] sessionId) {
        final StringBuilder id = new StringBuilder();
        for (byte b : sessionId) {
            id.append(String.format("%02X", b));
        }
        return id.toString();
    }

    public static void main(String[] args) throws GeneralSecurityException, IOException {
        final SessionResumptionChecker checker = new SessionResumptionChecker("TLSv1.2");
        final Result result = checker.checkSessionResumption(100, "www.facebook.com", 443);
        System.out.println(result);
    }
}
