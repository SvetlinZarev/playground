package com.github.svetlinzarev.playground.web.echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/**
 *
 */
public final class SimpleNioEchoClient {
    private static final int BUFFER_CAPACITY = 1024 * 8;
    private static final String MESSAGE = "Hello";

    public static void main(String[] args) throws IOException {
        final Selector selector = Selector.open();
        final InetSocketAddress destination = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        final SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(destination);
        socketChannel.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT);
        System.out.println("Connecting to: " + destination);

        while (selector.isOpen()) {
            final int numberOfSelectedKeys = selector.select();
            if (numberOfSelectedKeys > 0) {
                final Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    if (!selectionKey.isValid()) {
                        continue;
                    }

                    if (selectionKey.isConnectable()) {
                        connect(selectionKey);
                    } else if (selectionKey.isWritable()) {
                        write(selectionKey);
                    } else if (selectionKey.isReadable()) {
                        read(selectionKey);
                    }
                }
                selectionKeys.clear();
            }

            if (selector.keys().isEmpty()) {
                selector.close();
            }
        }

    }

    private static void connect(SelectionKey selectionKey) throws IOException {
        final SocketChannel channel = (SocketChannel) selectionKey.channel();
        if (channel.finishConnect()) {
            selectionKey.interestOps(SelectionKey.OP_WRITE);
            System.out.println("Finished connecting: " + channel);
        }
    }

    private static void write(SelectionKey selectionKey) throws IOException {
        final SocketChannel channel = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
        if (null == buffer) {
            buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
            selectionKey.attach(buffer);
        }

        buffer.put(MESSAGE.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        channel.write(buffer);
        buffer.compact();

        int operations = SelectionKey.OP_WRITE;
        if (buffer.position() == 0) {
            operations = SelectionKey.OP_READ;
        }

        selectionKey.interestOps(operations);

        if (SelectionKey.OP_READ == operations) {
            System.out.println("Written to: " + channel);
        }
    }

    private static void read(SelectionKey selectionKey) throws IOException {
        final SocketChannel channel = (SocketChannel) selectionKey.channel();
        final ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
        if (channel.read(buffer) == -1) {
            selectionKey.cancel();
            System.out.printf("Remote socket disconnected: " + channel);
            return;
        }

        int operations = SelectionKey.OP_WRITE;
        if (buffer.position() < MESSAGE.length()) {
            operations = SelectionKey.OP_READ;
        }

        selectionKey.interestOps(operations);

        if (SelectionKey.OP_WRITE == operations) {
            buffer.flip();
            final CharBuffer data = StandardCharsets.UTF_8.decode(buffer);
            buffer.clear();
            System.out.println("Read from: " + channel + ": " + data.toString());
        }
    }
}
