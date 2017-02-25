package com.github.svetlinzarev.playground.web.echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Set;

/**
 *
 */
public final class SimpleNioEchoServer {
    private static final int BUFFER_CAPACITY = 1024 * 8;

    public static void main(String[] args) throws IOException {
        final Selector selector = Selector.open();
        final InetSocketAddress destination = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
        final ServerSocketChannel acceptorChannel = ServerSocketChannel.open();
        final SelectableChannel selectableChannel = acceptorChannel.configureBlocking(false);
        selectableChannel.register(selector, SelectionKey.OP_ACCEPT);
        acceptorChannel.bind(destination);
        System.out.println("Listening on: " + destination);

        while (selector.isOpen()) {
            final int numberOfSelectedKeys = selector.select();
            if (numberOfSelectedKeys > 0) {
                final Set<SelectionKey> selectionKeys = selector.selectedKeys();
                for (SelectionKey selectionKey : selectionKeys) {
                    if (!selectionKey.isValid()) {
                        continue;
                    }

                    if (selectionKey.isAcceptable()) {
                        accept(selector, selectionKey);
                    } else if (selectionKey.isWritable()) {
                        write(selectionKey);
                    } else if (selectionKey.isReadable()) {
                        read(selectionKey);
                    }
                }
                selectionKeys.clear();
            }
        }
    }

    private static void accept(Selector selector, SelectionKey selectionKey) throws IOException {
        final ServerSocketChannel channel = (ServerSocketChannel) selectionKey.channel();
        final SocketChannel socketChannel = channel.accept();
        final SelectableChannel selectableChannel = socketChannel.configureBlocking(false);
        selectableChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("Accepted: " + socketChannel);
    }

    private static void write(SelectionKey selectionKey) throws IOException {
        final SocketChannel channel = (SocketChannel) selectionKey.channel();
        final ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
        buffer.flip();
        channel.write(buffer);
        buffer.compact();

        int operations = getInterestedOperations(buffer);
        selectionKey.interestOps(operations);
        System.out.println("Written to: " + channel);
    }

    private static void read(SelectionKey selectionKey) throws IOException {
        final SocketChannel channel = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = (ByteBuffer) selectionKey.attachment();
        if (null == buffer) {
            buffer = ByteBuffer.allocate(BUFFER_CAPACITY);
            selectionKey.attach(buffer);
        }

        if (channel.read(buffer) == -1) {
            selectionKey.cancel();
            System.out.printf("Remote socket disconnected: " + channel);
            return;
        }

        int operations = getInterestedOperations(buffer);
        selectionKey.interestOps(operations);
        System.out.println("Read from: " + channel);
    }

    private static int getInterestedOperations(ByteBuffer buffer) {
        int operations;
        if (buffer.position() == 0) {
            operations = SelectionKey.OP_READ;
        } else if (buffer.remaining() == 0) {
            operations = SelectionKey.OP_WRITE;
        } else {
            operations = SelectionKey.OP_READ | SelectionKey.OP_WRITE;
        }
        return operations;
    }
}
